package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.*;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueNote;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.exception.AlreadyIssuedException;
import lk.ijse.dep9.service.exception.LimitExceedException;
import lk.ijse.dep9.service.exception.NotAvailableException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IssueServiceImpl implements IssueService {

    private final MemberDAO memberDAO;

    private final BookDAO bookDAO;

    private final IssueNoteDAO issueNoteDAO;

    private final IssueItemDAO issueItemDAO;

    private final QueryDAO queryDAO;

    private final Converter converter;

    public IssueServiceImpl(Connection connection) {
        memberDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.MEMBER);
        bookDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.BOOK);
        issueNoteDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.ISSUE_NOTE);
        issueItemDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.ISSUE_ITEM);
        queryDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.QUERY);
        converter = new Converter();
        /* Associate the connection to the current thread */
        ConnectionUtil.setConnection(connection);
    }

    @Override
    public void placeNewIssueNote(IssueNoteDTO issueNoteDTO) throws NotFoundException, NotAvailableException, LimitExceedException, AlreadyIssuedException {
        /* Check the member existence */
        if (!memberDAO.existsById(issueNoteDTO.getMemberId())) {
            throw new NotFoundException("Member doesn't exist");
        }
        for (String isbn : issueNoteDTO.getBooks()) {
            /* Check the book existence */
            if (!bookDAO.existsById(isbn)) {
                throw new NotFoundException("Book: " + isbn + " doesn't exist");
            }
            /* Check the availability of the book */
            Integer availableCopies = queryDAO.getAvailableBookCopies(isbn).get();
            if (availableCopies == 0) {
                throw new NotAvailableException("Isbn no: " + isbn + " book is not available at the moment");
            }
            /* Check whether a book (in the issue note) has been already issued to this member */
            if (queryDAO.isAlreadyIssued(isbn, issueNoteDTO.getMemberId())) {
                throw new AlreadyIssuedException("Book: " + isbn + " has been already issued to the same member");
            }
        }
        /* Check how many books can be issued for this member (maximum = 3) */
        Integer availableLimit = queryDAO.availableBookLimit(issueNoteDTO.getMemberId()).get();
        if (availableLimit < issueNoteDTO.getBooks().size()) {
            throw new LimitExceedException("Member's book limit has been exceeded");
        }

        /* Begin transactions */
        try {
            ConnectionUtil.getConnection().setAutoCommit(false);

            /* Create issue note entity from IssueNoteDTO */
            IssueNote issueNote = converter.toIssueNoteEntity(issueNoteDTO);
            IssueNote savedIssueNote = issueNoteDAO.save(issueNote);

            /* Get saved issue note id (auto generated) and set it to the issue note dto */
            int issueNoteId = savedIssueNote.getId();
            issueNoteDTO.setId(issueNoteId);

            /* Create issue item entity list from issue note dto */
            List<IssueItem> issueItemList = converter.toIssueItemEntityList(issueNoteDTO);
            for (IssueItem issueItem : issueItemList) {
                issueItemDAO.save(issueItem);
            }
            ConnectionUtil.getConnection().commit();
        }
        catch (Throwable t) {
            try {
                ConnectionUtil.getConnection().rollback();
                throw new RuntimeException(t);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        finally {
            try {
                ConnectionUtil.getConnection().setAutoCommit(true);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
