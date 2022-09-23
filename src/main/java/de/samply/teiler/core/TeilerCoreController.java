package de.samply.teiler.core;

import de.samply.teiler.app.TeilerApp;
import de.samply.teiler.app.TeilerAppConfigurator;
import de.samply.teiler.utils.CorsChecker;
import de.samply.teiler.utils.ProjectVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TeilerCoreController {

    private final String projectVersion = ProjectVersion.getProjectVersion();
    private CorsChecker corsChecker;
    private TeilerAppConfigurator teilerAppConfigurator;
    private String defaultLanguage;

    @GetMapping(TeilerCoreConst.INFO_PATH)
    public ResponseEntity<String> info() {
        return new ResponseEntity<>(projectVersion, HttpStatus.OK);
    }

    @GetMapping(TeilerCoreConst.APPS_PATH)
    public ResponseEntity<TeilerApp[]> getApps (@PathVariable String language, HttpServletRequest request){

        HttpHeaders httpHeaders = createBasicHeaders(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(fetchApps(language), httpHeaders, HttpStatus.OK);

    }

    private TeilerApp[] fetchApps(String language) {
        if (language == null){
            language = defaultLanguage;
        }
        return teilerAppConfigurator.getTeilerApps(language).toArray(TeilerApp[]::new);
    }

    private HttpHeaders createBasicHeaders(HttpServletRequest request) {

        HttpHeaders httpHeaders = new HttpHeaders();

        String originUrl = request.getHeader(HttpHeaders.ORIGIN);
        if (corsChecker.isOriginUrlAllowed(originUrl)){
            httpHeaders.set("Access-Control-Allow-Origin", originUrl);
        }

        return httpHeaders;

    }

    @Autowired
    public void setCorsChecker(CorsChecker corsChecker) {
        this.corsChecker = corsChecker;
    }

    @Autowired
    public void setTeilerAppConfigurator(TeilerAppConfigurator teilerAppConfigurator) {
        this.teilerAppConfigurator = teilerAppConfigurator;
    }

    @Autowired
    public void setDefaultLanguage(@Value(TeilerCoreConst.DEFAULT_LANGUAGE_SV) String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

}
