package org.maxgamer.rs.tools.importers;

import org.maxgamer.rs.core.Core;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class XMLItemDefImporter {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, SQLException{
		File file = new File("data", "itemDefinitions667or718.txt");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		final FileInputStream fin = new FileInputStream(file);
		
		//builder.par
		//Document document = builder.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		InputStream in = new InputStream(){
			@Override
			public int read() throws IOException{
				int read = fin.read();
				if(read != -1 && read < 0x20 && !(read == 0x09 || read == 0x0A)){
					return 0x20;
				}
				return read;
			}
		};
		Document document = builder.parse(in);
		
		NodeList nodes = document.getDocumentElement().getChildNodes();
		
		for(int i = 0; i < nodes.getLength(); i++){
			//An <id> section
			Node node = nodes.item(i);
			
			if(node.getNodeName().equalsIgnoreCase("id") == false) continue;
			
			String examine = null;
			int id = -1;
			String name = null;
			boolean stack = false;
			boolean noted = false;
			int[] bonuses = new int[14];
			
			NodeList components = node.getChildNodes();
			
			for(int j = 0; j < components.getLength(); j++){
				Node child = components.item(j);
				
				if(child == null){
					continue;
				}
				
				if(child instanceof Element){
					Element element = (Element) child;
					String field = element.getNodeName().toLowerCase();
					
					if(field.equals("name")){
						name = element.getTextContent();
					}
					else if(field.equals("examine")){
						examine = element.getTextContent();
					}
					else if(field.equals("id")){
						id = Integer.parseInt(element.getTextContent());
					}
					else if(field.equals("noted")){
						noted = Boolean.parseBoolean(element.getTextContent());
					}
					else if(field.equals("stackable")){
						stack = Boolean.parseBoolean(element.getTextContent());
					}
					else if(field.equalsIgnoreCase("bonus") == false){
						System.out.println("Unrecognised field: " + field);
						System.exit(1);
					}
				}
				else if(child.getNodeName().equals("bonus")){
					NodeList bonusNodes = child.getChildNodes();
					for(int k = 0; k < bonusNodes.getLength(); k++){
						Node bonus = bonusNodes.item(k);
						bonuses[k] = Integer.parseInt(bonus.getTextContent());
					}
				}
			}
			System.out.println(name);
			
			Connection con = Core.getWorldDatabase().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO item_defs (id, name, examine, noted, maxStack, tradeable) VALUES (?, ?, ?, ?, ?, 0)");
			ps.setInt(1, id);
			ps.setString(2, name);
			ps.setString(3, examine);
			ps.setInt(4, noted ? 1 : 0);
			ps.setInt(5, stack ? Integer.MAX_VALUE : 1);
			ps.execute();
			ps.close();
			
			for(int j = 0; j < bonuses.length; j++){
				if(bonuses[j] != 0){
					PreparedStatement wep = con.prepareStatement("INSERT INTO item_weapons VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					wep.setInt(1, id);
					wep.setInt(2, 3); //Weapon
					wep.setInt(3, 0);
					wep.setInt(4, 0);
					wep.setInt(5, 0);
					wep.setInt(6, 0);
					
					for(int k = 0; k < bonuses.length; k++){
						wep.setInt(7 + k, bonuses[k]);
					}
					wep.setInt(7 + bonuses.length, 0);
					wep.setInt(7 + bonuses.length + 1, 0);
					wep.execute();
					wep.close();
					break;
				}
			}
		}
		fin.close();
		System.out.println("Done!");
	}
}
