//
//  ColorUtils.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 11.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import HamsterListCore
import SwiftUI

extension CSSColor {
    func toColor() -> Color {
        switch self {
            case let rgbaColor as CSSColor.RGBAColor:
                return Color(
                    UIColor(red: CGFloat(rgbaColor.red) / 255.0,
                            green: CGFloat(rgbaColor.green) / 255.0,
                            blue: CGFloat(rgbaColor.blue) / 255.0,
                            alpha: CGFloat(rgbaColor.alpha) / 255.0)
                )
            case let hslCollor as CSSColor.HSLColor:
                return Color(hue: hslCollor.hue / 360.0,
                      saturation: hslCollor.saturation,
                      brightness: hslCollor.lightness)
            default:
                let fallbackColor = ItemState.Companion.shared.DEFAULT_CATEGORY_COLOR
                return Color(
                    UIColor(red: CGFloat(fallbackColor.red) / 255.0,
                            green: CGFloat(fallbackColor.green) / 255.0,
                            blue: CGFloat(fallbackColor.blue) / 255.0,
                            alpha: CGFloat(fallbackColor.alpha) / 255.0)
                )
        }
    }
}
