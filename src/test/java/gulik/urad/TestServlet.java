package gulik.urad;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServlet;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import gulik.demo.VegetableServlet;
//import gulik.dolichos.*;

import org.junit.Test;

public class TestServlet  {
    
    @Test
    public void testServlet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/$metadata");
        request.addParameter("odata-debug", "json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // This doesn't work unfortunately. You still get HTML.
        // request.setContentType("application/json");


        HttpServlet sv = new VegetableServlet();
        sv.init(null);
        
        
        sv.service(request, response);
        response.getWriter().flush();
        String result = response.getContentAsString();
        System.out.println(result);
        
        assertTrue(result.contains("Vegetable"));
    }
}