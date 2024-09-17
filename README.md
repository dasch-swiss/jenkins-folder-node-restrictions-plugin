# Jenkins Folder Node Restrictions Plugin

This Jenkins plugin allows restricting which nodes jobs inside a folder can run on.  
The folder property can be configured to allow or disallow nodes based on label expressions.

### Usage

See Settings below for configuration via GUI.

Each folder has a Folder Node Restrictions property that can be configured.  
When allowed labels is enabled, jobs can only run on nodes if they're matched by the specified label expression.  
When disallowed labels is enabled, jobs cannot run on nodes if they're matched by the specified label expression.

If multiple folders are nested then all parent folders of the job must allow the job running on the node and none must disallow it.

The disallow labels expressions always take precedence over the allowed labels expressions.

### Settings

|Setting|Use|
|-|-|
|Job > Enable allowed labels|A label expression specifying which nodes the contained jobs can run on.|
|Job > Enable disallowed labels|A label expression specifying which nodes the contained jobs cannot run on.|


### Development

Starting a development Jenkins instance with this plugin: `mvn hpi:run`

Building the plugin: `mvn package`
