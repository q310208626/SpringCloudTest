package com.user.controller;

import com.user.bean.TRole;
import com.user.service.ITRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class TRoleController {

    @Autowired
    private ITRoleService roleService;

    @PostMapping
    public ResponseEntity<TRole> roleAdd(@RequestBody TRole role) {
        roleService.save(role);
        return ResponseEntity.ok(role);
    }
}
