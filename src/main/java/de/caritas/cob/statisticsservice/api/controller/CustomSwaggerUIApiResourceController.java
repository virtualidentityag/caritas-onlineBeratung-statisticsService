package de.caritas.cob.statisticsservice.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Controller
@ApiIgnore
@RequestMapping(value = "${springfox.docuPath}" + "/swagger-resources")
public class CustomSwaggerUIApiResourceController extends ApiResourceController {

  public static final String SWAGGER_UI_BASE_URL = "/statistics/docs";

  public CustomSwaggerUIApiResourceController(SwaggerResourcesProvider swaggerResources) {
    super(swaggerResources, SWAGGER_UI_BASE_URL);
  }

}
