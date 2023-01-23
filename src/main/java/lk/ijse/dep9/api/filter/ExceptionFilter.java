package lk.ijse.dep9.api.filter;

import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep9.api.exception.ResponseStatusException;
import lk.ijse.dep9.dto.ResponseStatusDTO;
import lk.ijse.dep9.service.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class ExceptionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(request, response, chain);
        }
        catch (Throwable t) {
            ResponseStatusException r = t instanceof ResponseStatusException ? (ResponseStatusException) t : null;
            if (r == null || r.getStatus() >= 500) {
                log.error(t.getMessage(), t);
            }
            ResponseStatusDTO statusDTO = new ResponseStatusDTO();
            statusDTO.setStatus(r == null ? 500 : r.getStatus());

            if (t instanceof LimitExceedException ||
                    t instanceof AlreadyReturnedException ||
                    t instanceof AlreadyIssuedException ||
                    t instanceof NotAvailableException) {
                statusDTO.setStatus(400);
            }
            else if (t instanceof NotFoundException) {
                statusDTO.setStatus(404);
            }
            else if (t instanceof InUseException || t instanceof DuplicateException) {
                statusDTO.setStatus(409);
            }

            statusDTO.setMessage(t.getMessage());
            statusDTO.setPath(request.getRequestURI());
            statusDTO.setTimestamp(new Date().getTime());

            response.setContentType("application/json");
            response.setStatus(statusDTO.getStatus());
            JsonbBuilder.create().toJson(statusDTO, response.getWriter());
        }
    }
}
