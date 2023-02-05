import ShoppingListCore
import SwiftUI

struct ContentView: View {
    @State private var listId = "Demo"

    var body: some View {
        NavigationView {
            VStack {
                TextField("Listname", text: $listId)
                    .disableAutocorrection(true)
                    .padding(8)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal, 16)
                NavigationLink("Load", destination: ShoppingListPage(listId: listId))
            }
        }
    }
}

struct ContentViewPreview: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
