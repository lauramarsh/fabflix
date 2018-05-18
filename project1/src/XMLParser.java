import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {
	
	//Database Info 
	static String loginUser = "root";
    static String loginPasswd = "pissoff";
    static String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
    static Connection connection;
    
    //Get the max movie id to use for movies w/o IDs
    static int maxMovieID;
    static int maxStarID;

    // set of movie names that have been parsed
	 private static Set<String> filmSet = new HashSet<String>();
	 // maps genre name to its id in the database 
	 private static Map<String, Integer> genreMap = new HashMap<>();
	 //maps movie name to list of genres names in that movies 
	 private static Map<String, List<Integer>> genresInMovies  = new HashMap<>();
	 private static Map<String, List<Integer>> starsInMovies = new HashMap<>();
	 
	 private static Set<String> stagenames = new HashSet<String>();
	
	 private static String getTextValue(Element ele, String tagName) {
	        
		 	String textVal = null;
		 	
		 	try
		 	{
		 		NodeList nl = ele.getElementsByTagName(tagName);
		        if (nl != null && nl.getLength() > 0) {
		            Element el = (Element) nl.item(0);
		            textVal = el.getFirstChild().getNodeValue();
		        }
		 	}
	        catch(Exception e)
		 	{
	        	return textVal;
		 	}

	        return textVal;
	    }

    private static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }
    
    // GENRE FUNCTIONS 
    public static int createGenre(String genre, String movieID)
    {
    	try 
		{		
    		
    		Statement statement = connection.createStatement();
			String query = "insert into genres(name) values('" + genre + "')";
			statement.executeUpdate(query);
			
			String selectQuery = "select id from genres where name = '" + genre + "'";
			ResultSet rs = statement.executeQuery(selectQuery);
			
			int genreID = -1;
			if(rs.next())
				genreID = rs.getInt("id");
			
			rs.close();
			statement.close();
			
			return genreID;
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
    }
    
    public static int getGenre(String genre){
		try 
		{
		
			Statement statement = connection.createStatement();
			
			String query = "select id from genres where name like '" + genre + "'";
		
			ResultSet rs = statement.executeQuery(query);
			if(rs.next())
				return rs.getInt("id");
			
			rs.close();
			statement.close();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
    
    public static boolean movieExists(String id)
    {
    	try 
		{
			Statement statement = connection.createStatement();
			String query = "select id from movies where id = '"+ id + "'";
			ResultSet rs = statement.executeQuery(query);
			
			if(rs.next())
				return true;
			statement.close();
			return false;
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    private static void createStarInMovie(String name, String movieId)
    {
    	
    	try 
		{
			Statement statement = connection.createStatement();
			String query = "select id from stars where name = '"+ name + "'";
			ResultSet rs = statement.executeQuery(query);
			
			String starID;
			if(rs.next())
			{
				starID = rs.getString("id");
				
				String insertQuery = "insert into stars_in_movies(starId, movieId) values('"+ starID + "','" + movieId + "')";
				statement.executeUpdate(insertQuery);
			}
			statement.close();
	
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    // STAR FUNCTIONS
    private static void parseStarsXML(File starsXML)
    {
    	
    	try
    	{
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(starsXML);
					
			doc.getDocumentElement().normalize();
			
			//get the root elememt
	        Element root = doc.getDocumentElement();
	
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
			//get list of directors (3094 directors)
	        NodeList directors = root.getElementsByTagName("dirfilms");
	        
	        for(int i = 0; i < directors.getLength(); i++)
	        {
	        	//Get a single director 
	        	Node director = directors.item(i);
	        	//
	        	if(director.getNodeType() == Node.ELEMENT_NODE)
	        	{
	        		Element directorElement = (Element) director;
	        		
	        		NodeList casts = directorElement.getChildNodes();
	        		
					for(int j = 0; j < casts.getLength(); j++)
					{
						//get a single cast item
						Node cast = casts.item(j);
						if(cast.getNodeType() == Node.ELEMENT_NODE)
						{
							Element castElement = (Element) cast;
							
							//go into the filmc portion of the cast object
							
							NodeList filmcList = castElement.getChildNodes();
							
							for(int k = 0; k < filmcList.getLength(); k++)
							{
								
								if(filmcList.item(k).getNodeType() == Node.ELEMENT_NODE)
								{
									Element filmc = (Element) filmcList.item(k);
									//System.out.println("Element: " + filmc.getTextContent());
									
									//get list of m entries, which specify star/movie connections
									NodeList fList = filmc.getElementsByTagName("f");
									NodeList aList = filmc.getElementsByTagName("a");
									for(int l = 0; l < fList.getLength(); l++)
									{
										// this is the movie id 
										Node f = fList.item(l);
										
										//this is the actor
										Node a = aList.item(l);
										System.out.println(f.getTextContent() + " " + a.getTextContent());
										
										if (a.getTextContent() == null)
										{
											continue;
										}
										String stagename = a.getTextContent();
										
										//check if star exists
										if(!starExists(stagename))
										{
											System.out.println("No such star in database.");
											continue;
										}
										else if(!movieExists(f.getTextContent()))
										{
											System.out.println("No such movie in database.");
											continue;
										}
										
										else
										{
											//add to database 
											createStarInMovie(stagename, f.getTextContent());
											
										}
									}
								}
							}
						
						}
					}
	        	
	        	}
	        }
	        	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    // ACTORS FUNCTIONS
    
    private static String generateStarID(int number)
    {
    	String ID = "nm" + number;
    	return ID;
    }
    
    private static int getMaxStarID()
    {
    	int returnID = -1;
		try 
		{		
    		Statement statement = connection.createStatement();
			String query = "select max(id) from stars";
			
			ResultSet rs = statement.executeQuery(query);
			
			String ID = rs.getString(1);
			@SuppressWarnings("resource")
			Scanner in = new Scanner(ID).useDelimiter("[^0-9]+");
			returnID = in.nextInt();
    	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnID;
    }
    
    public static boolean starExists(String name)
    {
    	try 
		{
			Statement statement = connection.createStatement();
			String query = "select id, name from stars where name = '"+ name + "'";
			ResultSet rs = statement.executeQuery(query);
			
			if(rs.next())
				return true;
			statement.close();
			return false;
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    public static void createStar(String name, String birthYear)
    {
    	try 
		{
    		String ID = generateStarID(++maxStarID);
    		
			Statement statement = connection.createStatement();
			String query = "insert into stars(id, name, birthYear)  values('" + ID + "','" + name + "', " + birthYear + ")";
			
			System.out.println(query);
		
			statement.executeUpdate(query);
			statement.close();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static boolean checkIfInt(String s)
    {
    	try
    	{
    		Integer.parseInt(s);
    		return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }
    private static void parseActorsXML(File actorsXML)
    {
    	try
    	{
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(actorsXML);
					
			doc.getDocumentElement().normalize();
			
			//get the root elememt
	        Element root = doc.getDocumentElement();
	
			System.out.println("Root element :" + root.getNodeName());
				
			//get list of actors
			NodeList actors = doc.getElementsByTagName("actor");
			for(int i = 0; i < actors.getLength(); i++)
			{
				
				Node actor = actors.item(i);
				if(actor.getNodeType() == Node.ELEMENT_NODE)
				{
					
					Element actorElement = (Element) actor;
					String birthYear = null;
					
					String stagename = actorElement.getElementsByTagName("stagename").item(0).getTextContent();
					// replace single quotes to avoid  query errors 
					stagename = stagename.replaceAll("'", " ");
					if(!stagenames.contains(stagename))
					{
						
						String dob = getTextValue(actorElement, "dob");
						if(dob == null || !checkIfInt(dob))
						{ 
							// just set to 0 if none provided
							birthYear = "0";
						}
						else
						{
							birthYear = dob;
						}
						stagenames.add(stagename);

						// Check if star exists in database
						try
						{
							if(starExists(stagename))
							{
								System.out.println("Star not added, already exists in database");
							}
							else
							{
								createStar(stagename, birthYear);
							}
						} 
						catch(Exception e)
						{
							System.out.println("Could not add star.");				
						}
					}
					else
					{
						//duplicate found in xml
						System.out.println("Star not added, already exists in database");
					}
				}
			}
					
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    // MOVIE FUNCTIONS 
    
    private static String generateMovieID(int number)
    {
    	String ID = "tt" + number;
    	return ID;
    }
    
    public static void createMovie(String ID, String director, String title, int year)
    {
    	try 
		{
		
			Statement statement = connection.createStatement();
			String query = "insert into movies(id, title, director, year)  values('" + ID + "', '" + title + "', '"
					+ director + "', " + year + ")";
		
			statement.executeUpdate(query);
			
			
			//for every genre that was in this movie add to genres_in_movies
			List<Integer> genres = genresInMovies.get(title);
			
			for(Integer genreID: genres)
			{
				if(genreID != -1)
				{
					//add to genres_in_movies
					String insertQuery = "insert into genres_in_movies(genreId, movieId) values("+ genreID + ",'" + ID + "')";
					statement.executeUpdate(insertQuery);
				}
			}
			
			//insert movie id into ratings table: this so that it will still show up on search page
			String insertQuery = "insert into ratings(rating, movieId) values(0.0,'" + ID + "')";
			statement.executeUpdate(insertQuery);
			
			statement.close();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private static int getMaxMovieID()
    {
    	int returnID = -1;
		try 
		{		
    		Statement statement = connection.createStatement();
			String query = "select max(id) from movies";
			
			ResultSet rs = statement.executeQuery(query);
			
			String ID = rs.getString(1);
			@SuppressWarnings("resource")
			Scanner in = new Scanner(ID).useDelimiter("[^0-9]+");
			returnID = in.nextInt();
    	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnID;
    }
    
    private static void parseMovieXML(File movieXML)
    {
    	try
    	{
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(movieXML);
					
			doc.getDocumentElement().normalize();
			
			//get the root elememt
	        Element root = doc.getDocumentElement();
	
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
			//get list of directors (3094 directors)
	        NodeList directors = root.getElementsByTagName("directorfilms");
	        
	        for(int i = 0; i < directors.getLength(); i++)
	        {
	        	System.out.println("Director #" + i);
	        	//Get a single director 
	        	Node director = directors.item(i);
	        	String dirname = null;
	        	//
	        	if(director.getNodeType() == Node.ELEMENT_NODE)
	        	{
	        		Element directorElement = (Element) director;
	        		//Get director name
	        		if(directorElement.getElementsByTagName("dirname").getLength() > 0)
	        		{
	        			dirname = directorElement.getElementsByTagName("dirname").item(0).getTextContent();
	        		}	        		
	        		//declare genre list in this scope
	        		List<Integer> genreIDList;
	        		
	        		// Get list of films by the director 
	        		NodeList films = directorElement.getElementsByTagName("film");
	        		for(int j = 0; j < films.getLength(); j++)
	        		{
	        		
	        			// Get a single film element
	        			Element film = (Element) films.item(j);
	        			
	        			//replace single quotes in title to avoid query error
	        			String title = getTextValue(film, "t");
	        					
	        			if(title == null)
	        			{
	        				System.out.println("Could not add movie. No title provided");
	        				continue;
	        			}	        			
	        			title = title.replaceAll("'", " ");
	        			System.out.println("\nMovie Title: " + title);
	        			
	        			//Add to hash set of film names
	        			filmSet.add(title.toLowerCase());
	        			
	        			// Get or create movieId
	        			String xmlID = getTextValue(film, "fid");
	        		
	        			//ID for inserting into table
	        			String ID = "";
	        			if(xmlID == null || xmlID.length() == 0)
	        			{
	        				//generate an ID
	        				System.out.println("No ID provided. Generating...");
	        				ID = generateMovieID(maxMovieID++);
	        			}
	        			else
	        			{
	        				ID = xmlID;
	        				System.out.println(ID);
	        			}
	        			// Get movie year 
	        			String yearString = getTextValue(film, "year");
	        			int year;
	        			 try 
	        			 { 
	        				 year = Integer.parseInt(yearString); 
	        			 } 
	        			 catch(Exception e) 
	        			 { 
	        			       System.out.println("Cannot add movie '" + title + "'. Year data malformed");
	        			       //continue to next film
	        			       continue;
	        			 }
	        			//Get genres
	        			NodeList genres = film.getElementsByTagName("cats");
	        			
	        			//Create a list of all genreIDs in this movie to add to genres_in_movies later
	        			genreIDList = new ArrayList<Integer>();
	        			
	        			for(int k = 0; k < genres.getLength(); k++)
	        			{
	        				// genre name
	        				 String genre = genres.item(k).getTextContent();
	        				 
	        				 // If there is a genre name
	        				 if(genre.length() != 0)
	        				 {
	        					 int genreID = -1;
	        					 
	        					 if(genreMap.containsKey(genre))
	        					 {
	        						 genreID = genreMap.get(genre);
	        					 }
	        					 else
	        					 {
	        						 //check if genre is in the database 
	        						 genreID = getGenre(genre);
	        					 }
	        					 
	        					 //if after both checks ID is still -1
	        					 // new genre must be created
	        					 if(genreID == -1)
	        					 {
	        						 genreID = createGenre(genre, ID);
	        					 }
	        					 
	        					 //Add genre id to list
	        					 genreIDList.add(genreID);

	        				 }
	        			}
	        			//add the list of genres ids to the genres in movies map
	        			genresInMovies.put(title, genreIDList);
	        			//create the movie
	        			createMovie(ID, dirname, title, year);
	        		}
	        	}
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

	 public static void main(String argv[]) {
		 
		 long startTime = System.nanoTime();
		 try 
		 {
			 	
		 	Class.forName("com.mysql.jdbc.Driver").newInstance();
    		connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
    		//get the max movie id to start with 
    		maxMovieID = getMaxMovieID();
    		maxStarID = 1000;
    		
			File movieXML = new File("src/mains243.xml");
			//parseMovieXML(movieXML);
		    
			File actorsXML = new File("src/actors63.xml");
			//parseActorsXML(actorsXML);
			
			File starsXML = new File("src/casts124.xml");
			//parseStarsXML(starsXML);
			connection.close();
		 }
		catch (Exception e) 
		 {
			e.printStackTrace();
	     }
		 
		 long endTime   = System.nanoTime();
		 long totalTime = endTime - startTime;
		 System.out.println("RunningTime: " + totalTime);
	 	
	 }
		
}
