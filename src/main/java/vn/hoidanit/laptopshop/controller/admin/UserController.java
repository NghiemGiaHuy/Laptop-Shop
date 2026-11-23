package vn.hoidanit.laptopshop.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.laptopshop.domain.Role;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = userService.getAllUsersByEmail("huy1@gmail.com");
        System.out.println(arrUsers);
        return "admin/user/home";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam(defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 1);
        Page<User> users = userService.getAllUsers(pageable);
        List<User> listUsers = users.getContent();
        model.addAttribute("users", listUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "admin/user/show";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("id", id);
        model.addAttribute("user", user);
        return "admin/user/detail";
    }

    @PostMapping(value = "/admin/user/create")
    public String createUser(@ModelAttribute("newUser") @Valid User user, BindingResult newUserBindingResult,
                             @RequestParam("myFile") MultipartFile file) {

        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }

        String avatar = uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setAvatar(avatar);
        user.setRole(userService.getRoleByName(user.getRole().getName()));
        userService.handleSaveUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = userService.getUserById(id);
        model.addAttribute("user", currentUser);
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUpdateUser(@ModelAttribute("user") User inforUser, @RequestParam("myFile") MultipartFile file) {
        User currentUser = userService.getUserById(inforUser.getId());
        String avatar = uploadService.handleSaveUploadFile(file, "avatar");
        Role role = userService.getRoleByName(inforUser.getRole().getName());
        if (currentUser != null) {
            currentUser.setFullName(inforUser.getFullName());
            currentUser.setAddress(inforUser.getAddress());
            currentUser.setPhone(inforUser.getPhone());
            currentUser.setRole(role);
            currentUser.setAvatar(avatar);
            userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("deleteUser", userService.getUserById(id));
        model.addAttribute("id", id);
        return "admin/user/delete.jsp";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(@ModelAttribute("deleteUser") User user) {
        userService.deleteUserById(user.getId());
        return "redirect:/admin/user";
    }
}
