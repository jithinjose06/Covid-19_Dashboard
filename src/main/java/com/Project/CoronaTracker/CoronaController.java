package com.Project.CoronaTracker;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.Project.CoronaTracker.CoronaService;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

import static javax.tools.StandardLocation.MODULE_PATH;

@Controller
public class CoronaController {
    CoronaService coronaService;

    public CoronaController(CoronaService coronaService) {
        this.coronaService = coronaService;
    }

    @GetMapping("/populate")
    public String testMethod(Model model){
        coronaService.populateDatabase();
        return "home";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {

        return "about";
    }

    @GetMapping("/home")
    public String root(Model model) {
        model.addAttribute("coronaData",coronaService.findAll());
        coronaService.executePython();
        return "home";
    }

    @GetMapping("/visual")
    public String secondPage( Model model)
    {
        return "visualization";
    }

}
