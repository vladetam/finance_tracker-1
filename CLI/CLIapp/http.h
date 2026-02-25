#pragma once
#include <iostream>
#include <thread>
#include <chrono>
#include <curl/curl.h>
#include <nlohmann/json.hpp>
using json = nlohmann::json;
size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* s) {
    s->append((char*)contents, size * nmemb);
    return size * nmemb;
}
/* UNUTAR FUNCKIJE KAKO SE KORISTI GET
    std::string response = httpGET("http://localhost:8080/api/transactions");

    try {
        json j = json::parse(response);
        std::cout << "Transactions count: " << j.size() << std::endl;
    }
    catch (const json::parse_error& e) {
        std::cout << "Invalid JSON: " << e.what() << "\n";
    }
*/
std::string httpGET(const std::string& url) {
    CURL* curl = curl_easy_init();
    std::string response;
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {                    // ← check immediately
            std::cerr << "curl error: "
                << curl_easy_strerror(res)
                << std::endl;
        }
        curl_easy_cleanup(curl);
    }
    return response;
}
std::string httpPOST(const std::string& url, const std::string& jsonBody) {
    CURL* curl = curl_easy_init();
    std::string response;
    if (curl) {
        struct curl_slist* headers = NULL;
        headers = curl_slist_append(headers, "Content-Type: application/json");
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonBody.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
        CURLcode res = curl_easy_perform(curl);

        if (res != CURLE_OK) {
            std::cerr << "curl error: "
                << curl_easy_strerror(res)
                << std::endl;
        }
        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);
    }
    return response;
}
void asyncGetLoop() { // TODO: from json store and check if there are new ones (in relation to local json file transactions.json) and display new ones, one per one
    while (true) {
        std::string response = httpGET("http://localhost:8080/api/transactions"); 
        try {
            auto j = json::parse(response);
            std::cout << "Transactions count: " << j.size() << std::endl;
        }
        catch (...) {
            std::cout << "Invalid JSON\n";
        }
        std::this_thread::sleep_for(std::chrono::seconds(5));
    }
}