package gov.nysenate.openleg.api.servlets.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.util.Config;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DataViewerServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(DataViewerServlet.class);
    private static Config appConfig;
    private static ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appConfig = Application.getConfig();
        objectMapper = Application.getObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String envDir = appConfig.getValue("env.directory");
        req.setAttribute("envDir", envDir);
        getServletContext().getRequestDispatcher("/admin/data-viewer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // Environment parameters
        String storageDir = appConfig.getValue("env.storage");
        String workDir = appConfig.getValue("env.work");
        String pubDir = storageDir + "/published";
        String unpubDir = storageDir + "/unpublished";

        // Request Parameters
        String docType = req.getParameter("docType");
        String year = req.getParameter("searchYear");
        String searchStr = req.getParameter("searchStr");

        String[] searchPaths = new String[] {
            pubDir + "/" + year.trim(),
            unpubDir + "/" + year.trim()
        };

        if (docType.equals("bill")) {
            File billFile;
            boolean isPublished = false;
            year = year.trim();
            String house = searchStr.substring(0, 1);
            String printNo = searchStr.substring(1);

            String jsonFilename = searchStr + "-" + year.trim() + ".json";

            // Look for the json file in either the published or unpublished dir
            billFile = FileUtils.getFile(searchPaths[0] + "/bill", jsonFilename);
            if (billFile.exists()) {
                isPublished = true;
            }
            else {
                billFile = FileUtils.getFile(searchPaths[1] + "/bill", jsonFilename);
            }

            if (billFile.exists()) {
                String jsonString = IOUtils.toString(new FileInputStream(billFile));
                req.setAttribute("isPublished", isPublished);
                req.setAttribute("billJson", jsonString);

                JsonNode root = objectMapper.readTree(jsonString);
                req.setAttribute("billId", root.get("billId").asText());
                List<File> sobiFiles = getSobiFiles(root, workDir);
                Map<String, String> sobiStrList = new LinkedHashMap<>();
                for (File sobiFile : sobiFiles) {
                    String sobiStr = IOUtils.toString(new FileInputStream(sobiFile));
                    String[] lines = sobiStr.split("(\r\n|\n)");
                    List<String> relevantLines = new ArrayList<>();
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        if (line.startsWith(year + house + StringUtils.leftPad(printNo, 5, '0'))) {
                            relevantLines.add("<span class='sobi-line-num'>" +
                                 StringUtils.leftPad(Integer.toString(i), 5, ' ') + "</span>" + line);
                        }
                    }
                    sobiStrList.put(sobiFile.getName(), StringUtils.join(relevantLines, "<br />"));
                }
                req.setAttribute("sobiListJson", objectMapper.writeValueAsString(sobiStrList));
            }
        }

        getServletContext().getRequestDispatcher("/admin/bill-data.jsp").forward(req, resp);
    }

    private List<File> getSobiFiles(JsonNode root, String workDir)
    {
        List<File> sobis = new ArrayList<>();
        Iterator<JsonNode> dataSources = root.get("dataSources").elements();
        while (dataSources.hasNext()) {
            sobis.add(FileUtils.getFile(workDir + "/bills", dataSources.next().asText()));
        }

        // Sort by date asc (filename has timestamp)
        Collections.sort(sobis, new Comparator<File>(){
            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });

        return sobis;
    }
}
