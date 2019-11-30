val samePackageSource = """
    package com.test.interfaces

    import com.test.interfaces.Model
    
    class InterfaceClass {
        val model = Model()
        println(model)
    }
""".trimIndent()

val allowedPackageSource = """
    package com.test.interfaces

    import com.test.application.Model
    
    class InterfaceClass {
        val model = Model()
        println(model)
    }
""".trimIndent()

val disallowedPackageSource = """
    package com.test.interfaces

    import com.test.domain.Model
        
    class InterfaceClass {
        val model = Model()
        println(model)
    }
""".trimIndent()