// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorSerial",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorSerial",
            targets: ["SerialPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SerialPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SerialPlugin"),
        .testTarget(
            name: "SerialPluginTests",
            dependencies: ["SerialPlugin"],
            path: "ios/Tests/SerialPluginTests")
    ]
)