package gulik.dolichos;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.debug.DebugInformation;
import org.apache.olingo.server.api.debug.DefaultDebugSupport;

import javax.servlet.ServletConfig;
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
    private static final long serialVersionUID = 202204221052L; 
    private static final Logger log = Logger.getLogger(ODataServlet.class.getCanonicalName());

    ODataHttpHandler handler;
    protected abstract List<ODataEntitySet> getEntitySets();

    
    public void init(ServletConfig config) throws ServletException {
        List<ODataEntitySet> entitySets = getEntitySets();

        OData odata = OData.newInstance();
        ServiceMetadata edm = odata.createServiceMetadata(new UradEdmProvider(entitySets), new ArrayList<EdmxReference>());
        this.handler = odata.createHandler(edm);
        handler.register(new PrintStacktraceDebugSupport());

        // For EntitySets
        handler.register(new DolichosEntityCollectionProcessor(entitySets));

        // For individual entities.
        handler.register(new DolichosEntityProcessor(entitySets));
    }

    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            // let the handler do the work
            handler.process(req, resp);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "Server Error occurred in ExampleServlet", e);
            throw new ServletException(e);
        }
    }

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

}
