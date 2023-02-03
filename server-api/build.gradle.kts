plugins { id("com.github.simonhauck.example.java-conventions") }

// We do not have java resources & get issues with task caching
tasks.compileJava { enabled = false }
