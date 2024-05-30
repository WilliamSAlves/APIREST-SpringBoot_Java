package com.devwillcode.apirest.controller;

import com.devwillcode.apirest.dto.UserRecordDto;
import com.devwillcode.apirest.model.UserModel;
import com.devwillcode.apirest.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<UserModel> cadastrarUser (@RequestBody @Valid UserRecordDto userRecordDto){
        var userModel = new UserModel();
        BeanUtils.copyProperties(userRecordDto, userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getAllUsers () {
        List<UserModel> userList = userRepository.findAll();
        if (!userList.isEmpty()) {
            for (UserModel user : userList) {
                UUID id = user.getId();
                user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById (@PathVariable(value = "id") UUID id) {
        Optional<UserModel> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum registro encontrado!");
        }
        userOptional.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("Users List!"));
        return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> atualizarUser (@PathVariable(value = "id") UUID id, @RequestBody @Valid UserRecordDto userRecordDto) {
        Optional<UserModel> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum resgistro encontrado!");
        }
        var userUpdated = userOptional.get();
        BeanUtils.copyProperties(userRecordDto, userUpdated);
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userUpdated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deletarUser (@PathVariable(value = "id") UUID id) {
        Optional<UserModel> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum registro encontrado!");
        }
        userRepository.delete(userOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Usuario excluido com sucesso!");
    }
}
