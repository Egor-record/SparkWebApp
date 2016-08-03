package com.siaskov;
import com.siaskov.model.CourseIdea;
import com.siaskov.model.CourseIdeaDAO;
import com.siaskov.model.NotFoundException;
import com.siaskov.model.SimpleCourseIdeaDAO;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.*;


/**
 * Created by EgorSiaskov on 29/07/16.
 */
public class main {
    public static void main(String[] args) {

        staticFileLocation("/public");

        CourseIdeaDAO dao = new SimpleCourseIdeaDAO();

        before((request, response) -> {
            if (request.cookie("username") != null) {
                request.attribute("username", request.cookie("username"));
            }
        });

        before("/ideas", (req, res) -> {
            if (req.cookie("username") == null) {
                res.redirect("/");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.attribute("username"));

            return new ModelAndView(model, "index.hbs");

        }, new HandlebarsTemplateEngine());

        //get("/hello", (req, res) -> "Hello World");


        post("/hhh", (req, res) -> {

            Map<String, String > model = new HashMap<>();
            String username = req.queryParams("username");
           res.cookie("username", username);

            model.put("username", req.queryParams("username"));


            return new ModelAndView(model, "hhh.hbs");
        }, new HandlebarsTemplateEngine());

        get("/ideas", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("ideas", dao.findAll());
            return new ModelAndView(model, "ideas.hbs");
        }, new HandlebarsTemplateEngine());


        post("/ideas", (req, res) -> {
            String title = req.queryParams("title");
            CourseIdea courseIdea = new CourseIdea(title, req.attribute("username"));
            dao.add(courseIdea);
            res.redirect("/ideas");
            return null;
        });

        post("/ideas/:slug/vote", (request, response) -> {
            CourseIdea idea = dao.findBySlug(request.params("slug"));

             idea.addVoter(request.attribute("username"));

            response.redirect("/ideas");
            return null;
        });

        get("/ideas/:slug", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("idea", dao.findBySlug(request.params("slug")));
            return new ModelAndView(model, "idea.hbs");

        }, new HandlebarsTemplateEngine());


        // Это не работает но мне лень искать почему
        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);

            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

            String html = engine.render(new ModelAndView(null, "notfound.hbs"));

            res.body(html);
        });

        /*post("/sing-in", (rq, rs) -> new ModelAndView(map, "index.hbs"), new HandlebarsTemplateEngine());*/
    }
}

