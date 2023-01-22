package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.exception.ResponseStatusException;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.MemberService;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "member-servlet", value = "/members/*")
public class MemberServlet extends NewHttpServlet {

    @Resource(lookup = "java:/comp/env/jdbc/lms_db")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            String query = request.getParameter("q");
            String size = request.getParameter("size");
            String page = request.getParameter("page");
            if (query != null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d++")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                }
                else {
                    searchMembersByPage(query, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            }
            else if (query != null && size == null && page == null) {
                searchMembers(query, response);
            }
            else if (query == null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                }
                else {
                    loadMembersByPage(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            }
            else {
                loadAllMembers(response);
            }
        }
        else {
            String pathInfo = request.getPathInfo();
            Matcher matcher = Pattern.compile("^/([A-Fa-f\\d]{8}(-[A-Fa-f\\d]{4}){3}-[A-Fa-f\\d]{12})/?$").matcher(pathInfo);
            if (matcher.matches()) {
                String uUID = matcher.group(1);
                getMemberDetails(uUID, response);
            }
            else {
                throw new ResponseStatusException(501);
            }
        }
    }

    private void loadAllMembers(HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            List<MemberDTO> allMembers = memberService.getAllMembers();
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(allMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMembersByPage(int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            List<MemberDTO> paginatedMembers = memberService.getMembersByPage(size, page);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(paginatedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchMembers(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            List<MemberDTO> searchedMembers = memberService.findMembers(query);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchMembersByPage(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            List<MemberDTO> searchPaginatedMembers = memberService.findMembersByPage(query, size, page);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchPaginatedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void getMemberDetails(String memberId, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            MemberDTO memberInfo = memberService.getMemberInfo(memberId);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(memberInfo, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(400, "Invalid Content Type");
            }
            else {
                try {
                    MemberDTO memberDTO = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);
                    createNewMember(memberDTO, response);
                }
                catch (JsonbException e) {
                    throw new ResponseStatusException(400, "Member JSON format is incorrect");
                }
            }
        }
        else {
            throw new ResponseStatusException(501);
        }
    }

    private void createNewMember(MemberDTO memberDTO, HttpServletResponse response) throws IOException {
        if (memberDTO.getId() != null) {
            throw new ResponseStatusException(400, "Member cannot have an ID, ID is auto generated");
        }
        else if (memberDTO.getName() == null || !memberDTO.getName().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Member name is empty or invalid");
        }
        else if (memberDTO.getAddress() == null || !memberDTO.getAddress().matches("^[A-Za-z\\d][A-Za-z\\d-|/# ,.:;\\\\]+$")) {
            throw new ResponseStatusException(400, "Member address is empty or invalid");
        }
        else if (memberDTO.getContact() == null || !memberDTO.getContact().matches("\\d{3}-\\d{7}")) {
            throw new ResponseStatusException(400, "Member contact is empty or invalid");
        }
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            MemberDTO savedMember = memberService.addNewMember(memberDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(savedMember, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            throw new ResponseStatusException(501);
        }
        String pathInfo = request.getPathInfo();
        Matcher matcher = Pattern.compile("^/([A-Fa-f\\d]{8}(-[A-Fa-f\\d]{4}){3}-[A-Fa-f\\d]{12})/?$").matcher(pathInfo);
        if (matcher.matches()) {
            String uUID = matcher.group(1);
            deleteMember(uUID, response);
        }
        else {
            throw new ResponseStatusException(400, "Invalid member UUID");
        }
    }

    private void deleteMember(String memberId, HttpServletResponse response) {
        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            memberService.deleteMember(memberId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            throw new ResponseStatusException(501);
        }
        String pathInfo = request.getPathInfo();
        Matcher matcher = Pattern.compile("^/([A-Fa-f\\d]{8}(-[A-Fa-f\\d]{4}){3}-[A-Fa-f\\d]{12})/?$").matcher(pathInfo);
        if (matcher.matches()) {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(400, "Invalid Content Type");
            }
            try {
                String uUID = matcher.group(1);
                MemberDTO memberDTO = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);
                if (!uUID.equals(memberDTO.getId())) {
                    throw new ResponseStatusException(400, "JSON object member id is not match with the url pattern member id");
                }
                updateMember(memberDTO, response);
            }
            catch (JsonbException e) {
                throw new ResponseStatusException(400, "Member JSON format is incorrect");
            }
        }
        else {
            throw new ResponseStatusException(400, "Invalid member UUID");
        }
    }

    private void updateMember(MemberDTO memberDTO, HttpServletResponse response) throws IOException {
        if (memberDTO.getName() == null || !memberDTO.getName().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Member name is empty or invalid");
        }
        else if (memberDTO.getAddress() == null || !memberDTO.getAddress().matches("^[A-Za-z\\d][A-Za-z\\d-|/# ,.:;\\\\]+$")) {
            throw new ResponseStatusException(400, "Member address is empty or invalid");
        }
        else if (memberDTO.getContact() == null || !memberDTO.getContact().matches("\\d{3}-\\d{7}")) {
            throw new ResponseStatusException(400, "Member contact is empty or invalid");
        }

        try (Connection connection = pool.getConnection()) {
            MemberService memberService = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
            MemberDTO updatedMember = memberService.updateMemberDetails(memberDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(updatedMember, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
