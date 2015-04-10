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
     
        // needed to avoid 404 error
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")
        
        // "/"(view:"/index") this was default
        "500"(view:'/error')
    }
}
