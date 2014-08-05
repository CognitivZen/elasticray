# ElasticRay

This is a liferay webs plugin for elastic search. It implements the interfaces as defined for liferay search plugins. 


## Usage

After deploying the plugin, ensure to point to the right URL of the elastic search server in "webapps\elastic-web\WEB-INF\classes\META-INF\elasticsearch-spring.xml". Modify the following lines to the correct elastic search server. Change serverIP and port to the required values.

 
    <bean id="com.rknowsys.portal.search.elastic.client.ClientFactory" class="com.rknowsys.portal.search.elastic.client.ClientFactoryImpl">
       <property name="port" value="9300"/>
        <property name="serverIP" value="127.0.0.1"/>
	
