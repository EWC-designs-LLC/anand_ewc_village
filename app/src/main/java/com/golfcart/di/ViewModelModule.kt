package com.golfcart.di

import com.golfcart.ui.address_place_search.AddressPlaceViewModel
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.contact_us.ContactUsViewModel
import com.golfcart.ui.detail.DetailViewModel
import com.golfcart.ui.favourite.FavouriteViewModel
import com.golfcart.ui.find_property.FindPropertyViewModel
import com.golfcart.ui.golf_cart_help.GolfCartHelpViewModel
import com.golfcart.ui.golf_course.GolfcourseViewModel
import com.golfcart.ui.golf_course_sub.GolfcourseSubcategoryViewModel
import com.golfcart.ui.home.HomeViewModel
import com.golfcart.ui.more.MoreViewModel
import com.golfcart.ui.navigation.NavigationViewModel
import com.golfcart.ui.notification.NotificationViewModel
import com.golfcart.ui.point_to_point.PointViewModel
import com.golfcart.ui.preview.PreviewViewModel
import com.golfcart.ui.rate_us.RateUsViewModel
import com.golfcart.ui.restaurant.RestaurantViewModel
import com.golfcart.ui.search.SearchViewModel
import com.golfcart.ui.share.ShareViewModel
import com.golfcart.ui.speedometer.SpeedometerViewModel
import com.golfcart.ui.splash.SplashViewModel
import com.golfcart.ui.subscription.SubscriptionViewModel
import com.golfcart.ui.terms.TermsAndConditionViewModel
import com.golfcart.ui.tutorial.TutorialViewModel
import com.golfcart.ui.version.AppVersionViewModel
import com.golfcart.ui.village.VillageViewModel
import com.golfcart.ui.weather.WeatherViewModel
import com.golfcart.ui.webview.WebViewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var viewModelModule = module {


    /**
     * Provide ViewModel object in activity Class
     * you can use it any Activity/Fragment class  below is declaration
     *
     * In Activity
     * private val baseViewModel: BaseViewModel by viewmodel() create object in activity scope
     *
     * In Fragment
     *  private val baseViewModel: BaseViewModel by viewmodel()  create object in fragment scope
     *
     *  Get object of activity scope use sharedViewModel()
     *  private val baseViewModel: BaseViewModel by sharedViewmodel()
     **/
    viewModel { BaseViewModel() }
    viewModel { SplashViewModel(get()) }
    viewModel { TermsAndConditionViewModel(get()) }
    viewModel { TutorialViewModel(get()) }
    viewModel { VillageViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { WeatherViewModel(get()) }
    viewModel { FavouriteViewModel(get()) }
    viewModel { RestaurantViewModel(get()) }
    viewModel { SpeedometerViewModel(get()) }
    viewModel { GolfcourseViewModel(get()) }
    viewModel { GolfcourseSubcategoryViewModel(get()) }
    viewModel { WebViewViewModel(get()) }
    viewModel { FindPropertyViewModel(get()) }
    viewModel { GolfCartHelpViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { AddressPlaceViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { MoreViewModel(get()) }
    viewModel { ShareViewModel(get()) }
    viewModel { ContactUsViewModel(get()) }
    viewModel { AppVersionViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { RateUsViewModel(get()) }
    viewModel { PreviewViewModel(get()) }
    viewModel { NavigationViewModel(get()) }
    viewModel { PointViewModel(get()) }
    viewModel { SubscriptionViewModel(get()) }
}