package co.pointred.core.startup;

/**
 * This class has to be re-done for MEMS.. there is no servlet for MEMS.
 * @author shajee
 *
 */

public class DatabaseBackupServlet //extends HttpServlet
{
	/**
	 * The Servlet for taking a backup of the Database....
	 */
	private static final long serialVersionUID = 1L;

	// public void doGet(HttpServletRequest request, HttpServletResponse
	// response) throws ServletException, IOException
	// {
	// String userName=request.getParameter("userName");
	// userName=userName.replace(' ','+');
	// ConcurrentHashMap<String,
	// PushBlockingQueue<Vector<ServerPushBaseObject>>>
	// dataHash=SingletonFactory.getSingletonFactory().getpushServiceRegister().getDataHash();
	// for(Map.Entry<String,PushBlockingQueue<Vector<ServerPushBaseObject>>> map
	// :dataHash.entrySet())
	// {
	// if(! map.getKey().equals(userName))
	// {
	// SingletonFactory.getSingletonFactory().getpushServiceRegister().unregister(map.getKey(),
	// ServerPushConstants.FORCE_LOG_OUT,"Database BackUp in process..");
	// }
	// }
	// String dt = new Date().toString();
	// dt = dt.replace(" ","_");
	// String fileName=Config.hibernateDir+"emsDbBackup_"+dt;
	// String downloadFileName="emsDbBackup_"+dt+".sql.gz";
	// if(createBackupFile(fileName)==true)
	// {
	// doDownload(request, response, fileName, downloadFileName);
	// }
	// }
	// private void doDownload(HttpServletRequest req, HttpServletResponse resp,
	// String filename, String downloadFileName) throws IOException
	// {
	// File f = new File(filename);
	// int length = 0;
	//
	// String contentDisposition = "attachment; filename=" + downloadFileName;
	// resp.setHeader("Content-disposition",contentDisposition);
	// ServletOutputStream op = resp.getOutputStream();
	//		
	// //
	// // Stream to the requester.
	// //
	// byte[] bbuf = new byte[1024];
	// DataInputStream in = new DataInputStream(new FileInputStream(f));
	//
	// while ((in != null) && ((length = in.read(bbuf)) != -1))
	// {
	// op.write(bbuf, 0, length);
	// }
	// in.close();
	// op.flush();
	// op.close();
	// boolean fileDelete = f.delete();
	// }
	//
	// private boolean createBackupFile(String fileName)
	// {
	// boolean retval = true;
	// BackUpDatabase bd = new BackUpDatabase();
	// retval = bd.backup(fileName);
	// return retval;
	// }
}