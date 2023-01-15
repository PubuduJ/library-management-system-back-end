package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.exception.ResponseStatusException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
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
                } else {
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
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Member");
            ArrayList<MemberDTO> allMembers = new ArrayList<>();

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO dto = new MemberDTO(id, name, address, contact);
                allMembers.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(allMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMembersByPage(int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement stmCount = connection.createStatement();
            ResultSet rstCount = stmCount.executeQuery("SELECT COUNT(id) FROM Member");
            rstCount.next();
            int totalMembers = rstCount.getInt(1);
            response.addIntHeader("X-Total-Count", totalMembers);

            PreparedStatement stmData = connection.prepareStatement("SELECT * FROM Member LIMIT ? OFFSET ?");
            stmData.setInt(1, size);
            stmData.setInt(2, (page - 1) * size);
            ResultSet rstData = stmData.executeQuery();

            ArrayList<MemberDTO> paginatedMembers = new ArrayList<>();
            while (rstData.next()) {
                String id = rstData.getString("id");
                String name = rstData.getString("name");
                String address = rstData.getString("address");
                String contact = rstData.getString("contact");
                MemberDTO dto = new MemberDTO(id, name, address, contact);
                paginatedMembers.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(paginatedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchMembers(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();

            ArrayList<MemberDTO> searchedMembers = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO dto = new MemberDTO(id, name, address, contact);
                searchedMembers.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchMembersByPage(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            query = "%" + query + "%";
            PreparedStatement stmCount = connection.prepareStatement("SELECT COUNT(id) FROM Member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            stmCount.setString(1, query);
            stmCount.setString(2, query);
            stmCount.setString(3, query);
            stmCount.setString(4, query);
            ResultSet rstCount = stmCount.executeQuery();
            rstCount.next();
            int searchedMemberCount = rstCount.getInt(1);
            response.addIntHeader("X-Total-Count", searchedMemberCount);

            PreparedStatement stmData = connection.prepareStatement("SELECT * FROM Member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");
            stmData.setString(1,query);
            stmData.setString(2,query);
            stmData.setString(3,query);
            stmData.setString(4,query);
            stmData.setInt(5,size);
            stmData.setInt(6,(page - 1) * size);
            ResultSet rstData = stmData.executeQuery();

            ArrayList<MemberDTO> searchPaginatedMembers = new ArrayList<>();
            while (rstData.next()) {
                String id = rstData.getString("id");
                String name = rstData.getString("name");
                String address = rstData.getString("address");
                String contact = rstData.getString("contact");
                MemberDTO dto = new MemberDTO(id, name, address, contact);
                searchPaginatedMembers.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchPaginatedMembers, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void getMemberDetails(String memberId, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member WHERE id=?");
            stm.setString(1, memberId);
            ResultSet rst = stm.executeQuery();

            if (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO member = new MemberDTO(id, name, address, contact);

                response.setContentType("application/json");
                JsonbBuilder.create().toJson(member, response.getWriter());
            }
            else {
                throw new ResponseStatusException(404, "Member UUID doesn't exist in the database");
            }
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
                    System.out.println(memberDTO.toString());
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
            memberDTO.setId(UUID.randomUUID().toString());
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Member (id, name, address, contact) VALUES (?, ?, ?, ?)");
            stm.setString(1, memberDTO.getId());
            stm.setString(2, memberDTO.getName());
            stm.setString(3, memberDTO.getAddress());
            stm.setString(4, memberDTO.getContact());

            int affectedRows = stm.executeUpdate();
            if (affectedRows == 1) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(memberDTO, response.getWriter());
            }
            else {
                throw new ResponseStatusException(500);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
