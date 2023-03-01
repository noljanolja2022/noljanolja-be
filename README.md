# Swagger Nolja
Nolja API

## Version: 1.0.0

**Contact information:**  
nguyenbrother9x@gmail.com

### /api/v1/conversations

#### POST
##### Summary:

create conversation

##### Description:

Create Conversation

##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| Authorization | header |  | Yes | string |
| body | body | conversation data | Yes | object |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | successful operation | object |

#### GET
##### Summary:

get conversation list

##### Description:

get Conversation list

##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| Authorization | header |  | Yes | string |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | successful operation | object |

### Models


#### Conversation

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | long |  | Yes |
| title | string |  | Yes |
| type | string |  | Yes |
| participants | [ [User](#User) ] |  | Yes |
| messages | [ [Message](#Message) ] |  | Yes |
| creator | [User](#User) |  | Yes |
| createdAt | string |  | Yes |
| updatedAt | string |  | Yes |

#### Message

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | long |  | Yes |
| message | string |  | Yes |
| attachments | [ [Attachment](#Attachment) ] |  | No |
| sender | [User](#User) |  | Yes |
| type | string |  | Yes |
| createdAt | string |  | Yes |

#### Attachment

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | long |  | Yes |
| type | string | Content type of the attachment | Yes |
| originalName | string |  | Yes |
| name | string |  | Yes |
| size | long |  | Yes |
| md5 | string |  | Yes |

#### Gif

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| url | string | url of the gif | No |
| dataSource | string | where the gif comes from | Yes |