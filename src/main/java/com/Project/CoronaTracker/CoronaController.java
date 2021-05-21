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

/**
 * Controls endpoints of the web-application
 */
@Controller
public class CoronaController {
    CoronaService coronaService;

    public CoronaController(CoronaService coronaService) {
        this.coronaService = coronaService;
    }

    /**
     * Method to populate the database
     * @return string representing the HTML template to be rendered
     */
    @GetMapping("/populate")
    public String populateData(){
        coronaService.populateDatabase();
        return "home";
    }

    /**
     * Method to display "about" page
     * @return string representing the HTML template to be rendered
     */
    @GetMapping("/about")
    public String aboutPage() {

        return "about";
    }

    /**
     * Method to display "Dashboard" page
     * @param model holder for model attributes
     * @return string representing the HTML template to be rendered
     */
    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("coronaData",coronaService.findAll());
        coronaService.executePython();
        return "home";
    }

    /**
     * Method to display "visualization" page
     * @return string representing the HTML template to be rendered
     */
    @GetMapping("/visual")
    public String visualPage()
    {
        return "visualization";
    }

}
