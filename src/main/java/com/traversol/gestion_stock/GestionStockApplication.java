package com.traversol.gestion_stock;

import com.traversol.gestion_stock.model.Rol;
import com.traversol.gestion_stock.model.Usuario;
import com.traversol.gestion_stock.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication

public class GestionStockApplication implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(GestionStockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            System.out.println("CREANDO USUARIOS DE PRUEBA...");

            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setEmail("admin@traversol.com");
            admin.setPassword(passwordEncoder.encode("Pedrozo123"));
            admin.setRol(Usuario.Rol.ADMINISTRADOR);
            usuarioRepository.save(admin);

            Usuario empleado = new Usuario();
            empleado.setNombre("Empleado");
            empleado.setEmail("empleado@traversol.com");
            empleado.setPassword(passwordEncoder.encode("Garcia123"));
            empleado.setRol(Usuario.Rol.EMPLEADO);
            usuarioRepository.save(empleado);

            Usuario gerente = new Usuario();
            gerente.setNombre("Gerente");
            gerente.setEmail("gerente@traversol.com");
            gerente.setPassword(passwordEncoder.encode("Pedrozo1234"));
            gerente.setRol(Usuario.Rol.ADMINISTRADOR);
            usuarioRepository.save(gerente);

            System.out.println("USUARIOS CREADOS CON ÉXITO");
        }
    }
}