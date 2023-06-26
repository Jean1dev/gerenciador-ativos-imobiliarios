package br.com.carteira.infra.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UsuarioContextFilter extends OncePerRequestFilter {

    private final ContextHolder holder;

    public UsuarioContextFilter(ContextHolder holder) {
        this.holder = holder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String user = request.getHeader("user");
        String email = request.getHeader("email");
        holder.setUserName(user);
        holder.setEmail(email);
        filterChain.doFilter(request, response);
    }
}
