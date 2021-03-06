import org.junit.*;

import java.util.*;

import play.mvc.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class FunctionalTest {

    @Test
    public void redirectHomePage() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Result result = route(controllers.routes.Application.index());

               assertThat(status(result)).isEqualTo(SEE_OTHER);
               assertThat(redirectLocation(result)).isEqualTo("/computers");
           }
        });
    }
    
    @Test
    public void listComputersOnTheFirstPage() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Result result = route(controllers.routes.Application.list(0, "name", "asc", ""));

               assertThat(status(result)).isEqualTo(OK);
               assertThat(contentAsString(result)).contains("574 computers found");
           }
        });
    }
    
    @Test
    public void filterComputerByName() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Result result = route(controllers.routes.Application.list(0, "name", "asc", "Apple"));

               assertThat(status(result)).isEqualTo(OK);
               assertThat(contentAsString(result)).contains("13 computers found");
           }
        });
    }
    
    @Test
    public void createANewComputer() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(controllers.routes.Application.save());

                assertThat(status(result)).isEqualTo(BAD_REQUEST);
                
                Map<String,String> data = new HashMap<String,String>();
                data.put("name", "FooBar");
                data.put("introduced", "badbadbad");
                data.put("company.id", "1");
                
                result = route(
                    fakeRequest(controllers.routes.Application.save()).bodyForm(data)
                );
                
                assertThat(status(result)).isEqualTo(BAD_REQUEST);
                assertThat(contentAsString(result)).contains("<option value=\"1\" selected=\"selected\">Apple Inc.</option>");
                assertThat(contentAsString(result)).contains("<input type=\"text\" id=\"introduced\" name=\"introduced\" value=\"badbadbad\" />");
                assertThat(contentAsString(result)).contains("<input type=\"text\" id=\"name\" name=\"name\" value=\"FooBar\" />");
                
                data.put("introduced", "2011-12-24");
                
                result = route(
                    fakeRequest(controllers.routes.Application.save()).bodyForm(data)
                );
                
                assertThat(status(result)).isEqualTo(SEE_OTHER);
                assertThat(redirectLocation(result)).isEqualTo("/computers");
                assertThat(flash(result).get("success")).isEqualTo("Computer FooBar has been created");
                
                result = route(controllers.routes.Application.list(0, "name", "asc", "FooBar"));
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentAsString(result)).contains("One computer found");
                
            }
        });
    }
    
}
