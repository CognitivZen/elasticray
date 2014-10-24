![Elasticray](https://github.com/R-Knowsys/elasticray/blob/master/elasticray-logo.png)
---
#####Free and Open Source Liferay Web Plugin for Elasticsearch(the flexible and powerful open source, distributed, real-time search and analytics engine). 

### License: This is released under the GNU-AGPL (Affero General Public License)

### How to use this plugin?
1) Install Elastic search server first. Ensure Java is installed in your machine.
	a) Download the latest version from here:
http://www.elasticsearch.org/download/
	b) Based on the OS, you can use the corresponding file. For windows get the zip file and unzip to a folder say ES_INSTALL_DIR
	c) Run elasticsearch.bat (if in windows) from ES_INSTALL_DIR/bin
	
2) Add the following lines to the portal-ext.properties file which point to the correct ip address and port of elastic search server. Restart liferay.

	elasticsearch.serverIP=127.0.0.1
	elasticsearch.portNumber=9300
	elasticsearch.cluster.name=elasticsearch

3) Download the Elasticray source from [here](https://github.com/R-Knowsys/elasticray/archive/master.zip) and deploy the plugin in your Liferay installation.

4) Once the installation is done, Do a "Reindex all search indexes" on from control panel -> Server Administration
	

### Development Status
Development is complete and right now the plugin is under testing. It is been submitted to the Liferay Marketplace.

