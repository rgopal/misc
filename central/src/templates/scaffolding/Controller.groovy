<%=packageName ? "package ${packageName}\n\n" : ''%>


import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured


@Secured(['ROLE_USER'])
class ${className}Controller {

    // remove "Instance" before calling a service
    // everything between <% %> will get evaluated, otherwise just
    // substituted for ${} enclosed variables
    
    <% def serviceName = propertyName.replaceFirst(/Instance$/, "") %>
    
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def ${serviceName}Service

    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [${propertyName}List: ${serviceName}Service.list(params),
            ${propertyName}Total: ${serviceName}Service.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base ${className}.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here ${propertyName})
        
        [${propertyName}: ${serviceName}Service.getNew(params)]
    }

 

    def save() {
        def ${propertyName} = ${serviceName}Service.create(params)
        if (!renderWithErrors('create', ${propertyName})) {
            redirectShow "${className} ${propertyName}.id created", ${propertyName}.id
        }
    }

    def show() {
        def ${propertyName} = findInstance()
        if (!${propertyName})  return

        [${propertyName}: ${propertyName}]
    }

    def edit() {
        def ${propertyName} = findInstance()
        if (!${propertyName}) return

        [${propertyName}: ${propertyName}]
    }

    
    def update() {
        def ${propertyName} = findInstance()
        if (!${propertyName}) return

        if(params.version) {
            def version = params.version.toLong()
            if(${propertyName}.version > version) {
                ${propertyName}.errors.rejectValue("version" ,
                    "${propertyName}.optimistic.locking.failure" ,
                    "Another user has updated this ${propertyName} " +
                    "while you were editing." )
                render(view:'edit' ,model:[${propertyName}:${propertyName}])
                return
            }
        }
        ${propertyName}.properties = params
        ${serviceName}Service.update ${propertyName}, params
        
        if (!renderWithErrors('edit', ${propertyName})) {
            redirectShow "${className} ${propertyName}.id updated", ${propertyName}.id
        }
    }
    def delete() {
        def ${propertyName} = findInstance()
        if (!${propertyName}) return

        try {
            ${serviceName}Service.delete ${propertyName}
            flash.message = "${className} " + params.id + " deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "${className} " + params.id + " could not be deleted", params.id
        }
    }
    def grant() {

        def ${propertyName} = findInstance()
        if (!${propertyName}) return

        if (!request.post) {
            return [${propertyName}: ${propertyName}]
        }

        ${serviceName}Service.addPermission(${propertyName}, params.recipient,
            params.int('permission'))

        redirectShow "Permission " + params.permission + " granted on ${className} " +
                    ${propertyName}.id  + " to " + params.recipient,  ${propertyName}.id
        
    }

    private ${className} findInstance() {
        def ${propertyName} = ${serviceName}Service.get(params.long('id'))
        if (!${propertyName}) {
            flash.message = "${className} not found with id " + params.id
            redirect action: index
        }
        ${propertyName}
    }

    
    private boolean renderWithErrors(String view, ${className} ${propertyName}) {
        if (${propertyName}.hasErrors()) {
            render view: view, model: [${propertyName}: ${propertyName}]
            return true
        }
        false
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

 
}
