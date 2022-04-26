package gulik.urad;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import gulik.demo.VegetableServlet;
//import gulik.dolichos.*;

import org.junit.Test;

public class TestServlet  {

    /** Fetch that URL and check that the result contains the given string. 
     * @throws IOException
     * @throws ServletException */
    private void checkForResponse(String url, String contains) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", url);
        request.addParameter("odata-debug", "json");
        request.addParameter("format", "json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // This doesn't work unfortunately. You still get HTML.
        // request.setContentType("application/json");

        HttpServlet sv = new VegetableServlet();
        sv.init(null);
        
        sv.service(request, response);
        response.getWriter().flush();
        String result = response.getContentAsString();
        System.out.println(result);
        
        assertTrue(result.contains(contains));
    }

    @Test
    public void testServlet() throws Exception {
        checkForResponse("/$metadata", "Vegetable");
    }

    @Test
    public void testFruitProperty() throws Exception {
        checkForResponse("/Fruit", "numberOfSeeds");
    }

    @Test
    public void testVegetables() throws Exception {
        checkForResponse("/Vegetables", "cabbage");
    }

    @Test
    public void testVegetable() throws Exception {
        checkForResponse("/Vegetables('brusselsprout')", "green");
    }
}