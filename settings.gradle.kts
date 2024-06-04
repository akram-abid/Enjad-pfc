pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        /*maven{
            url = uri("https://mapbox.com/downloads/v2/releases/maven")
            credentials.username="mapbox"
            credentials.password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            authentication.create<BasicAuthentication>("basic")
        }*/
    }
}

rootProject.name = "depannage"
include(":app")
 