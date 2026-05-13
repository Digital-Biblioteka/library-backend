package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.BookAccessRequest;
import nsu.library.entity.BookPermission;
import nsu.library.service.bookpermissions.PermissionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/books/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    //TODO: юзер делает реквест и библиотекарь его аппрувит по идее? дто нужна
    @PostMapping()
    public BookPermission GivePermission(@RequestBody BookAccessRequest req) {
        return null;
    }
}
