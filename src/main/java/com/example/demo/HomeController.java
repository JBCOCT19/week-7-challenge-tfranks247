package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;


    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid
                                          @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }
    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }



    @Autowired
    UserRepository userRepository;

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
    String username = principal.getName();
    model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    /////////////////// Copied in all of other HC

    @RequestMapping("/list")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm( @ModelAttribute Message message1, @Valid  Message message, BindingResult result,
                               @RequestParam("file") MultipartFile file
    ) {
        if (result.hasErrors()) {
            return "messageform";
        }
        if (file.isEmpty() && message1.getImage() == null) {
            return "redirect:/add";
        }
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                message.setImage(uploadResult.get("url").toString());
                messageRepository.save(message);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else
            messageRepository.save(message1);
        return "redirect:/list";
    }

    @RequestMapping("/list/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delmessage(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "/list";                             /////////////// changed from redirect:/
    }
}
