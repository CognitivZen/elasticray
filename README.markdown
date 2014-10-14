## Elasticray
#####Free and Open Source Liferay Web Plugin for Elasticsearch(the flexible and powerful open source, distributed, real-time search and analytics engine). 


### How to use this plugin?
1) Download the Elasticray source from [here](https://github.com/R-Knowsys/elasticray/archive/master.zip) and deploy the plugin in your Liferay installation.

2) Configure Elasticsearch server URL in elasticsearch-spring.xml
	Go to "webapps\elastic-web\WEB-INF\classes\META-INF\elasticsearch-spring.xml". 
	Modify the following lines to the correct Elasticsearch server. Change serverIP and port to the required values.
	Change Properties as required.
 
	    <bean id="com.rknowsys.portal.search.elastic.client.ClientFactory" 	 class="com.rknowsys.portal.search.elastic.client.ClientFactoryImpl">
	       <property name="port" value="9300"/>
	       <property name="serverIP" value="127.0.0.1"/>
	       <property name="properties">
	            <props>
	                <prop key="cluster.name">elasticsearch</prop>
	            </props>
	       </property>
	

### Development Status
Development is complete and right now the plugin is under testing. It is been submitted to the Liferay Marketplace.
