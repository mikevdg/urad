package gulik.dolichos;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.debug.DebugInformation;
import org.apache.olingo.server.api.debug.DefaultDebugSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ODataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // TODO
    private static final Logger log = Logger.getLogger(ODataServlet.class.getCanonicalName());

    protected abstract List<ODataEntity> getEntities();

    // TODO: only for development.
    private class PrintStacktraceDebugSupport extends DefaultDebugSupport {
        @Override
        public ODataResponse createDebugResponse(final String debugFormat, final DebugInformation debugInfo) {
            Exception e = debugInfo.getException();
            if (null!=e) {
                log.log(Level.WARNING, "OData error:", debugInfo.getException());
            }
            return super.createDebugResponse(debugFormat, debugInfo);
        }
    }

    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            // TODO: these are regenerated on every request! Is that expensive?

            // create odata handler and configure it with CsdlEdmProvider and Processor
            OData odata = OData.newInstance();
            // TODO: Hard-coded vegetables???
            // TODO: Multiple entities?
            ServiceMetadata edm = odata.createServiceMetadata(new UradEdmProvider(gulik.demo.VegetableEntitySet.class), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);

            handler.register(new PrintStacktraceDebugSupport());
            handler.register(new DolichosEntityCollectionProcessor(gulik.demo.VegetableEntitySet.class));
            handler.register(new DolichosEntityProcessor(gulik.demo.VegetableEntitySet.class));
            // let the handler do the work
            handler.process(req, resp);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "Server Error occurred in ExampleServlet", e);
            throw new ServletException(e);
        }
    }
}
