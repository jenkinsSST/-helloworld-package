# Directory Contents Manifest

This directory contains the following files:

## 7.8.0-fields.json

The result of executing the following command on a locally installed Jira Server 7.8.0:

http://10.70.0.75:8080/rest/api/2/field

> Returns a list of all fields, both System and Custom

See: https://docs.atlassian.com/software/jira/docs/api/REST/7.8.0/#api/2/field-getFields 

## 7.8.0-Task-createmeta.json

The result of executing the following command on a locally installed Jira Server 7.8.0:
    
http://10.70.0.75:8080/rest/api/2/issue/createmeta?projectKeys=TP&issuetypeNames=Task&expand=projects.issuetypes.fields
    
>Returns the meta data for creating issues. This includes the available projects, 
issue types and fields, including field types and whether or not those fields 
are required. Projects will not be returned if the user does not have permission to create issues in that project.

>The fields in the createmeta correspond to the fields in the create screen for the project/issuetype. Fields not in the screen will not be in the createmeta.

>Fields will only be returned if expand=projects.issuetypes.fields.

>The results can be filtered by project and/or issue type, given by the query params."
    
See: https://docs.atlassian.com/software/jira/docs/api/REST/7.8.0/#api/2/issue-getCreateIssueMeta

## 7.8.0-TP-112-editmeta.json 

The result of executing the following command on a locally installed Jira Server 7.8.0:

http://10.70.0.75:8080/rest/api/2/issue/TP-112/editmeta

>Returns the meta data for editing an issue. The fields in the editmeta 
correspond to the fields in the edit screen for the issue. Fields not in the screen will not be in the editmeta.
    
See: https://docs.atlassian.com/software/jira/docs/api/REST/7.8.0/#api/2/issue-getEditIssueMeta
    
## 7.8.0-TP-112-fields.json

The result of executing the following command on a locally installed Jira Server 7.8.0:
    
http://10.70.0.75:8080/rest/api/2/issue/TP-112/?expand=transitions.fields
    
>Returns a full representation of the issue for the given issue key. An issue
JSON consists of the issue key, a collection of fields, a link to the workflow
transition sub-resource, and (optionally) the HTML rendered values of any fields that support it (e.g. if wiki syntax is enabled for the description or comments).
    
See: https://docs.atlassian.com/software/jira/docs/api/REST/7.8.0/#api/2/issue-getIssue
    
## 7.8.0-TP-project.json

The result of executing the following command on a locally installed Jira Server 7.8.0:
    
http://10.70.0.75:8080/rest/api/2/project/TP
    
>Contains a full representation of a project in JSON format. All project keys associated  with the project will only be returned if expand=projectKeys.

See: https://docs.atlassian.com/software/jira/docs/api/REST/7.8.0/#api/2/project-getProject
    
## Creating Issues

The following list of fields is based on the default project and issue definitions when creating an Agile Scrum project and "Task" issue via Jira Server 7.8.0.

Note that both creation and update allow a request body that can include both a "fields" object and an "update" object.

### Required fields on create for the above issue type

- "project" { "name" : "TP" }
- "summary" "This fixes that"
- "issuetype" { "name" : "Task" }
- "reporter" { "name" : "user1" }

### Optional fields on create

- "components"
- "description"
- "fixVersions"
- "priority"
- "labels"
- "attachment"
- "issuelinks"
- "assignee"

## Updating Issues

### Issue fields available for update

- "summary"
- "components"
- "description"
- "reporter" 
- "fix versions"
- "customfield_10100" (Epic Link)
- "priority"
- "labels"
- "customfield_10104" (Sprint)
- "issuelinks"
- "comment" (op: add, edit, remove - requires use of "update" object)
- "assignee" 


## All fields available via GET

- "issuetype"
- "components"
- "timespent"
- "timeoriginalestimate"
- "description"
- "project"
- "fixVersions"
- "aggregatetimespent"
- "resolution"
- "timetracking"
- "customfield_10104"
- "customfield_10105"
- "attachment"
- "aggregatetimeestimate"
- "customfield_10109"
- "resolutiondate"
- "workratio"
- "summary"
- "lastViewed"
- "watches"
- "creator"
- "subtasks"
- "created"
- "reporter"
- "customfield_10000"
- "aggregateprogress"
- "priority"
- "customfield_10100"
- "labels"
- "environment"
- "timeestimate"
- "aggregatetimeoriginalestimate"
- "versions"
- "duedate"
- "progress"
- "comment"
- "issuelinks"
- "votes"
- "worklog"
- "assignee"
- "updated"
- "status"
- "transitions"


   