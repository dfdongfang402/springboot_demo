package com.generator;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class DiffWithSvn {
	
	public static Set<String> getDiffXls(String xlsdir) throws Exception {
		Set<String> diffs = new HashSet<String>();
		SVNClientManager svn = SVNClientManager.newInstance();
		SVNStatusClient statusclient = svn.getStatusClient();
		File fatherpath = new File(xlsdir);
		addModifiedFiles(statusclient, fatherpath, diffs);
		return diffs;
	}
	
	private static void addModifiedFiles(SVNStatusClient statusclient, File fatherpath, Set<String> diffs) throws Exception{
		for (File file : fatherpath.listFiles()) {
			if (file.isDirectory()) {
				if (file.getName().equals(".svn"))
					continue;
				addModifiedFiles(statusclient, file, diffs);
				continue;
			}
			try {
				SVNStatus status = statusclient.doStatus(file, false);
				if (status.getContentsStatus() == SVNStatusType.STATUS_MODIFIED ||
						status.getContentsStatus() == SVNStatusType.STATUS_ADDED) {
					String filepath = file.getPath();
					filepath = filepath.substring(Main.xlspath.length() + 1).replace('\\', '/').trim();
					diffs.add(filepath);
					System.out.println(filepath+":modified");
				}
			} catch (org.tmatesoft.svn.core.SVNException e) {
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Set<String> getDiffGenCodeXmls(String genBeanDir) throws Exception {
		Set<String> diffs = new HashSet<String>();
		SVNClientManager svn = SVNClientManager.newInstance();
		SVNStatusClient statusclient = svn.getStatusClient();
		File fatherpath = new File(genBeanDir);
		for (File file : fatherpath.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			SVNStatus status = statusclient.doStatus(file, false);
			if (status.getContentsStatus() == SVNStatusType.STATUS_MODIFIED ||
					status.getContentsStatus() == SVNStatusType.STATUS_ADDED) {
				diffs.add(file.getName());
				System.out.println(file.getName()+":modified");
			}
		}
		return diffs;
	}
	
	public static void main(String[] args) throws Exception {
		SVNClientManager svn = SVNClientManager.newInstance();
		SVNRepositoryFactoryImpl.setup();
		DAVRepositoryFactory.setup();
		ISVNAuthenticationManager auth = new BasicAuthenticationManager("", "");
		svn.setAuthenticationManager(auth);
		svn.getWCClient().doCleanup(new File("xlsdir"));
		SVNUpdateClient updateclient = svn.getUpdateClient();
		updateclient.setIgnoreExternals(false);
		
		updateclient.doUpdate(new File("xlsdir"), SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
		svn.getWCClient().doCleanup(new File("xlsdir"));
		System.out.println(updateclient);
		
	}
}
