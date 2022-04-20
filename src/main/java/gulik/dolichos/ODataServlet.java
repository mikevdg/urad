package gulik.dolichos;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ODataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ODataServlet.class.getCanonicalName());

    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            // TODO: these are regenerated on every request! Is that expensive?

            // create odata handler and configure it with CsdlEdmProvider and Processor
            OData odata = OData.newInstance();
            // TODO: Hard-coded vegetables???
            // TODO: Multiple entities?
            ServiceMetadata edm = odata.createServiceMetadata(new UradEdmProvider(gulik.demo.VegetableEndpoint.class), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register(new DolichosEntityCollectionProcessor(gulik.demo.VegetableEndpoint.class));
            handler.register(new DolichosEntityProcessor(gulik.demo.VegetableEndpoint.class));
            // let the handler do the work
            handler.process(req, resp);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "Server Error occurred in ExampleServlet", e);
            throw new ServletException(e);
        }
    }
}
