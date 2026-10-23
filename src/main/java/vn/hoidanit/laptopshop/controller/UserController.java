package vn.hoidanit.laptopshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@Controller
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = userService.getAllUsersByEmail("huy1@gmail.com");
        System.out.println(arrUsers);
        return "admin/user/home";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("user1", users);
        return "admin/user/tableUser";
    }

    @GetMapping("admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("id", id);
        model.addAttribute("user", user);
        return "admin/user/userDetail";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String createUserPage(Model model, @ModelAttribute("newUser") User user) {
        System.out.println("Create user" + user);
        this.userService.handleSaveUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @GetMapping("admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = userService.getUserById(id);
        model.addAttribute("user", currentUser);
        return "admin/user/updateUser";
    }

    @PostMapping("admin/user/update")
    public String postUpdateUser(@ModelAttribute("user") User inforUser) {
        User currentUser = userService.getUserById(inforUser.getId());
        if (currentUser != null) {
            currentUser.setFullName(inforUser.getFullName());
            currentUser.setAddress(inforUser.getAddress());
            currentUser.setPhone(inforUser.getPhone());
            userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("deleteUser", userService.getUserById(id));
        model.addAttribute("id", id);
        return "admin/user/deleteUser";
    }

    @PostMapping("admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("deleteUser") User user) {
        userService.deleteUserById(user.getId());
        return "redirect:/admin/user";
    }
}
