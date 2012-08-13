class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        '/'(controller: 'changeset')
        '/register'(controller: 'user', action: 'create')
        '500'(view: '/error')
    }
}
