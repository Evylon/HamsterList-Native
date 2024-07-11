import ShoppingListCore
import SwiftUI

struct ContentView: View {
    @State private var listId = "Demo"

    var body: some View {
        NavigationView {
            VStack {
                TextField("Listname", text: $listId)
                    .disableAutocorrection(true)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 16)
                NavigationLink("Load", destination: ShoppingListPage(listId: listId))
                    .padding(.top, 8)
            }
        }
    }
}

struct ContentViewPreview: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
