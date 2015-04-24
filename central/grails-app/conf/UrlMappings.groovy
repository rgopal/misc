class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/" (view:'/index') {
            constraints {
                
            }
        }
    
      "403"(controller: "errors", action: "error403")
      "500"(controller: "errors", action: "error500")
      "500"(controller: "errors", action: "error403",
            exception: AccessDeniedException)
      "500"(controller: "errors", action: "error403",
            exception: NotFoundException)
        
        // needed to avoid 404 error
       // "/login/$action?"(controller: "login")
       // "/logout/$action?"(controller: "logout")
        
        // "/"(view:"/index") this was default
       // "500"(view:'/error')
    }
}
