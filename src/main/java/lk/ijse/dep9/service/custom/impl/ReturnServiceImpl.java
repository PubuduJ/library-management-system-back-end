package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.QueryDAO;
import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.dto.ReturnItemDTO;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.service.exception.AlreadyReturnedException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class ReturnServiceImpl implements ReturnService {

    private final QueryDAO queryDAO;
    private final ReturnDAO returnDAO;
    private final Converter converter;

    public ReturnServiceImpl(Connection connection) {
        queryDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.QUERY);
        returnDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.RETURN);
        converter = new Converter();
        /* Set the connection */
        ConnectionUtil.setConnection(connection);
    }

    @Override
    public void updateReturnStatus(ReturnDTO returnDTO) throws NotFoundException {
        try {
            ConnectionUtil.getConnection().setAutoCommit(false);
            for (ReturnItemDTO returnItemDTO : returnDTO.getReturnItems()) {
                if (!queryDAO.isValidIssueItem(returnDTO.getMemberId(),
                        returnItemDTO.getIssueNoteId(), returnItemDTO.getIsbn())) {
                    throw new NotFoundException(String.format("Either member: %s, issue note id: %s, isbn: %s don't exist or this return item is not belong to this member",
                            returnDTO.getMemberId(),
                            returnItemDTO.getIssueNoteId(),
                            returnItemDTO.getIsbn()));
                }
                if (returnDAO.existsById(new ReturnPK(returnItemDTO.getIssueNoteId(), returnItemDTO.getIsbn()))) {
                    throw new AlreadyReturnedException("This " + returnItemDTO.getIsbn() +  " have been already returned");
                }
                Return returnEntity = converter.toReturnEntity(returnItemDTO);
                returnDAO.save(returnEntity);
            }
            ConnectionUtil.getConnection().commit();
            System.out.println("Yes");
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
