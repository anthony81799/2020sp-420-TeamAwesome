package model;

//System imports
import java.util.HashMap; 
import java.util.Map;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;
import java.io.Serializable;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * For adding and removing classes from the UML diagram
 * @author antho
 * @author Dylan
 */
public class UMLClassManager implements Serializable {
	// Version ID for serialization
	private static final long serialVersionUID = 2L;
	
	private HashMap<String, UMLClass> classList;
	private HashMap<String, UMLRelationship> relationships;

	/**
	 * Default constructor if we don't have a linked list make one
	 */
	public UMLClassManager() {
		classList = new HashMap<String, UMLClass>();
		relationships = new HashMap<String, UMLRelationship>();
	}
  
	public boolean empty() {
		return classList.isEmpty();
	}
	/**
	 * Adds node of type UMLClass to list
	 * @param name: name of class
	 * @return 0 if the new class was successfully added to the list
	 */
	public int addClass(String name) 
	{
		//check if name is valid
		if (!validName(name))
		{
			return 407;
		}
		//Prevent duplicates
		if (classList.containsKey(name))
		{
			return 200;
		}
		UMLClass newClass = new UMLClass(name);
		classList.put(name, newClass);
		return 0;
	}
	/**
	 * 
	 * @param className - the class we want to add a method to
	 * @param methodName - the name of the new method
	 * @return 0 on success and corresponding error code else
	 */
	public int addMethods(String className, String methodName)
	{

		if (classList.containsKey(className))
		{
			if (classList.get(className).getMethods().contains(methodName)) 
			{
				return 402;
			}
			else 
			{
				classList.get(className).addMethod(methodName);
				return 0;
			}
		}
		else return 403;
		
	}

		/**
	 * 
	 * @param className - the class we want to add a field to
	 * @param fieldName - the name of the new field
	 * @return 0 on success and corresponding error code else
	 */
	public int addFields(String className, String fieldName)
	{
		if (classList.containsKey(className))
		{
			if (classList.get(className).getFields().contains(fieldName)) 
			{
				return 404;
			}
			else 
			{
				classList.get(className).addField(fieldName);
				return 0;
			}
		}
		else return 403;
	}
	/**
	 * 
	 * @param className - class to remove field from
	 * @param fieldName - fieldname to remove
	 * @return - returns 0 on successfull removal and corresponding error code in all other cases
	 */
	public int removeFields(String className, String fieldName)
	{
		if (classList.containsKey(className))
		{
			if (classList.get(className).getFields().contains(fieldName)) 
			{
				classList.get(className).removeField(fieldName);
				return 0;
			}
			else 
			{
				return 405;
			}
		}
		else return 403;
	}

	/**
	 * 
	 * @param className - the class we want to remove a method from
	 * @param methodName - the name of the method we want to remove
	 * @return - returns 0 on success and corresponding error codes else
	 */
	public int removeMethods(String className, String methodName)
	{

		if (classList.containsKey(className))
		{
			if (classList.get(className).getMethods().contains(methodName)) 
			{
				classList.get(className).removeMethod(methodName);
				return 0;
			}
			else 
			{
				return 406;
			}
		}
		else return 403;
		
	}
	/**
	 * 
	 * @param oldName; the class we want to edit 
	 * @param newName; the name of the new class
	 * @return 0 on succesful name change and error code on failure 
	 */
	public int editClass(String oldName, String newName)
	{
			//check if the new name doesn't already exist as a class name
		if (classList.containsKey(newName))
		{
			return 400;
		}
		if (classList.containsKey(oldName))
		{
			UMLClass tempCopy = classList.get(oldName);
			tempCopy.setName(newName);
			classList.remove(oldName);
			classList.put(newName, tempCopy);
			return 0;
		}
		return 401;
	}
		/**
	 * @param className; the class holding the field we want to edit
	 * @param oldField; the field we want to change 
	 * @param newName; the name of the new field
	 * @return 0 on successful name change and error code on failure 
	 */
	public int editFields(String className, String oldField, String newName)
	{
			//check if the new name doesn't already exist as a class name
		if (classList.containsKey(className)){
			if (classList.get(className).getFields().contains(newName))
			{
				return 404; 
			}
			if (classList.get(className).getFields().contains(oldField))
			{
				//this is great code don't question it keep moving
				classList.get(className).removeField(oldField);
				classList.get(className).addField(newName);
				return 0;
			}
			return 405;
		}
		return 403;
	}

	/**
	 * 
	 * @param className - the class containing the method to edit
	 * @param oldMethod - the method name to edit
	 * @param newName - the new method name
	 * @return - 0 on successful 'name change' and corresponding error codes in all other cases
	 */
	public int editMethods(String className, String oldMethod, String newName)
	{
			//check if the new name doesn't already exist as a class name
		if (classList.containsKey(className)){
			if (classList.get(className).getMethods().contains(newName))
			{
				return 402; 
			}
			if (classList.get(className).getMethods().contains(oldMethod))
			{
				//this is great code don't question it keep moving
				classList.get(className).removeMethod(oldMethod);
				classList.get(className).addMethod(newName);
				return 0;
			}
			return 406;
		}
		return 403;
	}
	
	/**
	 * Removes node of type UMLClass from list
	 * @param className: name of class
	 * @return 0 if the class was successfully removed from the list
	 */
	public int removeClass(String className) {
		if (classList.containsKey(className))
		{
			// Remove the class from the list of classes
			classList.remove(className);
			
			// Remove any relationship involving the class
			relationships.entrySet().removeIf(e -> e.getValue().hasClass(className));
			
			return 0;
		}
		return 201;
	}
	
	/**
	 * Get the list of classes in the UML diagram
	 * @return String of classes in format "[class1, class2, ...]"
	 */
	public String listClasses() {
		String result = "[";
		
		// Iterate through classList
		int count = 0;
		for(Map.Entry<String, UMLClass> entry : classList.entrySet()) {
			result += entry.getKey();
			// Only add comma if not the last element
			if(count++ != classList.size() -1) {
				result += ", ";
			}
		}
		
		result += "]";
		return result;
	}

	public boolean validName(String name)
	{
		
		if (name == null || name.isEmpty()){
			return false;
		}
		Pattern specialSearch = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = specialSearch.matcher(name);
		boolean specialChar = m.find();
		if (specialChar){
			return false;
		}
		if (Character.isLetter(name.charAt(0)) && !name.contains(" ")){
			return true;
		}
		return false;
	}
	
	/**
	 * List the fields of the given class
	 * @param className
	 * @return 'tuple' of [string of fields, return code];
	 */
	public Object[] listFields(String className) {
		// Make sure class exists
		if(!classList.containsKey(className))
			return new Object[]{"", 109};
		
		return new Object[]{classList.get(className).getFields(), 0};
	}
	
	/**
	 * List the methods of the given class
	 * @param className
	 * @return 'tuple' of [string of methods, return code];
	 */
	public Object[] listMethods(String className) {
		// Make sure class exists
		if(!classList.containsKey(className))
			return new Object[]{"", 109};
		
		return new Object[]{classList.get(className).getMethods(), 0};
	}
	
	/**
	 * Create a relationship between the two given classes
	 * @param srcClass - the first class's name
	 * @param destClass - the second class's name
	 * @return 0 if successfully added relationship, error code otherwise
	 */
	public int addRelationship(String srcClass, String destClass) {
		// Make sure both class names exist
		if(!classList.containsKey(srcClass) || !classList.containsKey(destClass))
			return 107;
		
		// Make sure a relationship between both classes does not exist
		if(relationshipExists(srcClass, destClass))
			return 106;
		
		// If both classes exist and do not have a pre-existing relationship, then
		//		create a new relationship between them
		String key = UMLRelationship.GENERATE_STRING(srcClass, destClass);
		UMLRelationship relation = new UMLRelationship(classList.get(srcClass), classList.get(destClass));
		relationships.put(key, relation);
		
		// Indicate success
		return 0;
	}
	
	/**
	 * Remove the relationship between the two given classes
	 * @param srcClass - the first class's name
	 * @param destClass - the second class's name
	 * @return 0 if successfully removed the relationship, error code if otherwise
	 */
	public int removeRelationship(String srcClass, String destClass) {
		// Make sure both class name exist
		if(!classList.containsKey(srcClass) || !classList.containsKey(destClass))
			return 107;
		
		// Make sure there is a pre-existing relationship
		if(!relationshipExists(srcClass, destClass))
			return 108;
		
		// Determine which class is the key in the relationships map
		String key = UMLRelationship.GENERATE_STRING(srcClass, destClass);
		if(!relationships.containsKey(key))
			key = UMLRelationship.GENERATE_STRING(destClass, srcClass);
		
		// Remove the relationship from the map
		relationships.remove(key);
		
		return 0;
	}
	
	/**
	 * List the relationships the given class has
	 * @param className
	 * @return 'tuple' of [string of relationships, return code];
	 */
	public Object[] listRelationships(String className) {
		// Make sure class exists
		if(!classList.containsKey(className))
			return new Object[]{"", 107};
		
		// Find all relationships with className involved
		String result = "[";
		
		// Loop through all relationships checking if className is in each relationship, if
		//		it is then add the key to the output
		for(Map.Entry<String, UMLRelationship> relation : relationships.entrySet()) {
			// Check if className is in the relationship
			if(relation.getValue().hasClass(className))
				result += relation.getKey() +", ";
		}
		
		// Remove last ', ' if it exists
		if(result.endsWith(", "))
			result = result.substring(0, result.lastIndexOf(", "));
		
		result += "]";
		
		return new Object[]{result, 0};
	}
	
	/**
	 * Method to determine if there is already a relationship between the two classes
	 * @param class1
	 * @param class2
	 * @return true if a relationship exists, false if not
	 */
	private boolean relationshipExists(String class1, String class2) {
		// Iterate through relationship checking both directions (class1 -> class2) and (class1 <- class2)
		String direct1 = UMLRelationship.GENERATE_STRING(class1, class2);  
		String direct2 = UMLRelationship.GENERATE_STRING(class2, class1);  
		
		return relationships.containsKey(direct1) || relationships.containsKey(direct2);
	}
	
	/**
	 * Set the location of a class
	 * @param className - the class to set location
	 * @param x - new x coordinate
	 * @param y - new y coordinate
	 */
	public int setClassLocation(String className, int x, int y) {
		if(classList.containsKey(className)) {
			classList.get(className).setLocation(x, y);
			return 0;
		}
		
		return 109;
	}
	
	/**
	 * Convert the class list to a JSON string
	 * @return - JSON string
	 */
	public String convertToJSON() {
		String jsonString = "";
		
		// Create JSON builder and enable 'pretty printing' for multiple lines
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Convert manager to JSON
		jsonString += gson.toJson(this);
		
		return jsonString;
	}
	
	/**
	 * Parse JSON into classList
	 * @return true if parsed successfully
	 */
	public int parseJSON(String json) {
		// JSON parser object
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Deep clone the manager
		Type type = new TypeToken<UMLClassManager>(){}.getType();
		UMLClassManager clonedManager = gson.fromJson(json, type);
		
		// Set classList and relationships
		classList = clonedManager.getClassList();
		relationships = clonedManager.getRelationships();
		
		return 0;
	}
	
	/**
	 * Get the UMLClass with className
	 * @param className - name of class
	 * @return - UMLClass instance
	 */
	public UMLClass getClass(String className) {
		if(classList.containsKey(className))
			return classList.get(className);
		return null;
	}

	/**
	 * Get the map of classes
	 * @return - classList
	 */
	protected HashMap<String, UMLClass> getClassList() {
		return classList;
	}

	/**
	 * Get the map of relationships
	 * @return - relationships
	 */
	protected HashMap<String, UMLRelationship> getRelationships() {
		return relationships;
	}
	
	
}