package app.lucas.backend_cnab.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.lucas.backend_cnab.web.service.CnabService;

@RestController
@RequestMapping("cnab")
public class CnabController {

    private final CnabService cnabService;

    public CnabController(CnabService cnabService) {
        this.cnabService = cnabService;
    }
    /*
     * O método upload() recebe um arquivo MultipartFile e chama o método uploadCnabFile() do serviço CnabService.
     * O método uploadCnabFile() recebe um arquivo MultipartFile e chama o método transferTo() para transferir o arquivo para o local de armazenamento.
     */
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        cnabService.uploadCnabFile(file);
        return "Processamento iniciado!";
    }
    
}
