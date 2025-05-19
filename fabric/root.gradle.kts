plugins {
    id("gg.essential.multi-version.root")
}

preprocess {
    val fabric12105 = createNode("1.21.5", 12105, "yarn")
    val fabric12101 = createNode("1.21.1", 12101, "yarn")
    val fabric12001 = createNode("1.20.1", 12001, "yarn")
    val fabric11903 = createNode("1.19.3", 11903, "yarn")

    strictExtraMappings.set(true)
    fabric12101.link(fabric12105, null)
    fabric12001.link(fabric12105, null)
    fabric11903.link(fabric12105, null)
}