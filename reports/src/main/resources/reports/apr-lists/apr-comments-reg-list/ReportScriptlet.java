import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import ru.intertrust.cm.core.business.api.dto.impl.RdbmsId;

public class ReportScriptlet extends JRDefaultScriptlet {

  private static final String sqlRkkSubjectQuery = 
                " select rkk.subject " +
	        " from f_dp_rkkbase rkk " +
	        " join nunid2punid_map n2p on substring(n2p.punid, 1, 4)::int = rkk.id_type and substring(n2p.punid, 5, 12)::bigint = rkk.id " +
	        " join apr_list al on substring(n2p.nunid, 1, 32) = substring(al.rkkid, 1, 32) " +
	        " where al.id = (SELECT Item FROM QR_Id_List WHERE Request = _#_ order by id desc limit 1)"; 


  public String getRKKSubject(Connection connection, Object request) throws Exception {
    Long requestId = getId(request);
    String result = "";
    PreparedStatement statement = connection.prepareStatement(sqlRkkSubjectQuery.replace("_#_", requestId.toString()));
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
      result = resultSet.getString("subject");
    }
    resultSet.close();
    statement.close();
    return result;
  }

  private Long getId(Object dbmsId) {
    Long returnLong = -1L;
    if (dbmsId instanceof RdbmsId) {
      RdbmsId id = (RdbmsId) dbmsId;
      returnLong = id.getId();
    } else if (dbmsId instanceof List && !((List)dbmsId).isEmpty())  {
      returnLong = ((RdbmsId)((List)dbmsId).get(0)).getId();
    }
    return returnLong;
  }
}
