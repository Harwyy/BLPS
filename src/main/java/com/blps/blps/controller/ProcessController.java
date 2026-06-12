package com.blps.blps.controller;

import com.blps.blps.security.service.XmlUserDetailsService;
import io.camunda.client.CamundaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class ProcessController {

    private final CamundaClient camundaClient;
    private final XmlUserDetailsService userDetailsService;

    @PostMapping("/start")
    public ResponseEntity<String> startProcess(Authentication authentication) {

        camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("client_process")
                .latestVersion()
                .variable("client_id", userDetailsService.getXmlUser(authentication.getName()).getReferenceId().toString())
                .send()
                .join();

        return ResponseEntity.ok("Процесс успешно запущен");
    }

}
