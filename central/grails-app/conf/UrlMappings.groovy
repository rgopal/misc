class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/" (controller:'person') {
            constraints {
            }
        }
        
        // "/"(view:"/index") this was default
        "500"(view:'/error')
    }
}
