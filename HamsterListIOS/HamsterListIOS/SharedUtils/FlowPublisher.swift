//
//  FlowPublisher.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 26.02.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Combine

class FlowPublisher<Output, Failure: Error> : ObservableObject {
    private let publisher: AnyPublisher<Output, Failure>
    private var cancellable: AnyCancellable?
    @Published private(set) var value: Output

    init(publisher: AnyPublisher<Output, Failure>, initial: Output) {
        self.publisher = publisher
        self.value = initial
    }
    
    deinit {
        cancellable?.cancel()
    }

    func subscribePublisher() {
        self.cancellable = publisher
            .receive(on: DispatchQueue.main)
            .sink { completion in
                print("Received completion: \(completion)")
            } receiveValue: { newState in
                self.value = newState
            }
    }
}
