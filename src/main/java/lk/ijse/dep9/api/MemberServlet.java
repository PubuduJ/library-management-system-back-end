package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.exception.ResponseStatusException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    private void loadMembersByPage(int size, int page, HttpServletResponse response) {

    }

    private void searchMembers(String query, HttpServletResponse response) {

    }

    private void searchMembersByPage(String query, int size, int page, HttpServletResponse response) {

    }

    private void getMemberDetails(String uUID, HttpServletResponse response) {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
