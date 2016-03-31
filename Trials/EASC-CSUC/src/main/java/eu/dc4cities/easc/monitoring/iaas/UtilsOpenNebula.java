package eu.dc4cities.easc.monitoring.iaas;

import org.opennebula.client.Client;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.host.Host;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class UtilsOpenNebula{
	private static Client c=null;
	public UtilsOpenNebula(){
		try{
			if (c==null) c = new Client(ConstantsOne.one_user+":"+ConstantsOne.one_password, ConstantsOne.ONE_API_URL);
		}catch (Exception e){e.printStackTrace();}
		
	}

	public List<String> HostsEnable(){
		List<String> listHosts = new ArrayList();
		HostPool hostp = new HostPool(c);
		hostp.info();
		Iterator<Host> ithost = hostp.iterator();
		Host host;
		while (ithost.hasNext()){
			host=ithost.next();
			if (host.isEnabled()) listHosts.add(host.getName());
		}
		return listHosts;
	}
}
