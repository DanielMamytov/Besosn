package com.besosn.app.presentation.ui.articles

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment(R.layout.fragment_article_detail) {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//Training Loads Without Burnout
//Modern grassroots teams often copy professional calendars and burn out by the third week. Replace that with a rolling two‑week microcycle that alternates high and medium loads and never stacks two highs. Give each session a single objective — “break lines through midfield,” “press on back pass,” or “protect zone 14” — and make the scoring system reward that objective so the game teaches the lesson. Start with a six‑minute ball‑centric activation, then 20–25 minutes of opposed drills with clear constraints, and finish with small‑sided games. Collect RPE 1–10 three minutes after practice plus a one‑word sleep note; if RPE rises twice while sleep drops, cut the next load by 30% and shorten the tactical block, not the free play at the end. Rotate intensity by unit: defenders handle aerials and body positioning when forwards do repeat finishing. Keep warm‑ups football‑specific — rondos with directional targets, scanning cues (“shoulders open before receive”), and passing lanes that force checking movement. Publish the plan so parents know why a Tuesday felt light. Add monthly “fresh weeks” where you drop volume and chase speed and technique. The aim is not to suffer; it’s to show up on Sunday fresher than the opponent and still excited to train Monday. Sustainable rhythm beats hero sessions, and consistency — wins the long season.
//
//Статья 2:
//
//Selecting a Captain That Leads Quietly
//Body: A captain is rarely your loudest voice; it’s the person teammates copy when a match turns chaotic. Scout three behaviors all week: arriving early without reminders, neutral body language after mistakes, and questions that move play forward (“Who covers six when we press?”). Appoint a leadership trio — captain, deputy, and a younger voice — so influence crosses age groups and units. Give micro‑responsibilities: kit check, warm‑up tempo, and a coin‑toss ritual that anchors focus. Share match objectives privately on Friday so the armband isn’t a speech but a contract. In games, the captain speaks in verbs — “hold, slide, screen, switch” — and models recovery after errors: acknowledge, reset, re‑engage. After losses, they gather the back line for two minutes before anyone touches a phone and highlight one controllable for next session. Run monthly one‑to‑ones that review three moments: a conflict handled well, a communication miss, and a referee interaction. Rotate the set‑piece caller so authority isn’t hoarded. Leadership here is choreography more than charisma, repetition over rhetoric, and credibility earned in slow minutes between drills. Quiet leaders who do ordinary things consistently create extraordinary standards.
//
//Статья 3:
//
//Inventory That Wins You Points
//Equipment fails at the worst moment, so treat it like a substitute player with roles and readiness. Split items into A (match‑critical: balls, pumps, nets, bibs, tape, first‑aid) and B (training helpers: cones, hurdles, ladders, mannequins). Tag status for each piece: OK, Needs Fix, or Lost. Run a Friday ritual where two people sign off on every A‑item; dual control prevents the “I thought you packed it” catastrophe. When something shifts to Needs Fix, log a short note (“pump leaking”), assign an owner, and set a deadline. Store balls by pressure, not number; print the PSI on the crate and keep a mini pump in the captain’s bag. Color‑code cones to match warm‑up layouts so set‑up takes seconds. Photograph shelf layouts and pin them in the locker room for quick resets. Build a simple QR list on your phone; scan after training to update quantities and conditions. Keep a sacrificial “mud kit” for wet days so the match set stays clean. The payoff is hidden: saved warm‑up minutes become early chances, reduced friction lifts mood, and fewer panics protect focus. Tidy inventory isn’t pedantry — it’s points.
//
//Статья 4:
//
//Designing Set Plays for Amateurs
//Throw away the 20‑page restart bible. Create three corners and two free‑kicks your squad can execute under noise and rain. Use colors, not jargon: Blue = near‑post crowd, Red = screen the keeper, Gold = late run from the edge. Rehearse with a 15‑second shot clock so players decide, not debate. Assign one checker who aborts if the signal fails; chaos is worse than a normal cross. Build variations by swapping the runner and the blocker or flipping delivery side, not by inventing new routines weekly. Train under match realism — one ball, limited time, defenders trying to spoil. Add a scoring rule in practice: goals count double if the first touch after the set piece is in the six‑yard box. Film one rep each week and keep only the best two plays “live” at any time; retire and refresh monthly. Simplicity executed with conviction beats complexity forgotten under pressure, and your scoreboard will show it.
//
//Статья 5:
//
//Data Lite: Notes Coaches Actually Use
//You don’t need GPS vests to get smarter. Track five metrics you can capture from the bench: final‑third entries, shots on target, PPDA in the middle third, set‑piece threat, and goalkeeper distribution success. Use paper hash marks during games and digitize later. On Monday, review for ten minutes, select one actionable tweak (“invert the 8s on goal kicks,” “press after back pass”), and test it next match. Add a simple four‑game trend view to see if the tweak sticks. Avoid dashboards nobody reads; one printed chart in the locker room beats a spreadsheet graveyard. Keep definitions stable so players trust the numbers. Data is a compass, not a whip: it should guide questions, not deliver punishments. Small, repeatable notes compound into big gains that feel like common sense by season’s end.
//
//Статья 6:
//
//Scouting Opponents with a Phone
//Watch fifteen minutes of their latest match — ten in possession, five on set pieces. Note build‑up shape, preferred outlets, and which fullback they bait. Record pressing triggers (first touch to fullback? back pass? heavy touch from the six?). Freeze‑frame corners to mark runs and blockers, then sketch your counter‑screens. Tag their “get‑out” player — the one trusted to break pressure. Your plan becomes three rules: deny the get‑out, force play where you’re strong, and attack their least mobile defender. Share a two‑slide brief with one picture per rule and two sentences of instruction. Anything longer will be ignored on amateur schedules. Close the loop on Sunday night: did we follow the rules, and were they the right ones? Iteration over encyclopedias — always.
//
//Статья 7:
//
//Managing Minutes Across a Season
//Players want minutes that matter and bodies that last. Build monthly color bands: green (fit), amber (risk), red(returning). Cap amber players at 45 per game and pair them with trusted subs. Track cumulative minutes for U18s to avoid exam‑week spikes. Publish rotation plans on Thursday so Saturday’s bench understands the why. Use “impact roles” to make fewer minutes meaningful — late pressing sub, set‑piece specialist, game‑closing midfielder. Log wellness once a week; when three flags (sleep, soreness, stress) stack, shift the role before injuries do it for you. Honest, predictable fairness creates buy‑in and keeps the group coherent through dips in form.
//
//Статья 8:
//
//Culture Without Cliches
//Culture is the behaviors you reward, not a slogan. Ban sarcasm at training — it kills risk‑taking. Start meetings with a one‑sentence thank‑you round to surface invisible work. Post five non‑negotiables: be on time, compete in every drill, help with gear, respect officials, leave the bench clean. Enforce gently but always. Celebrate boring excellence like tidy kit, crisp warm‑ups, and fast set‑ups. Build rituals: a photo for first starts, a handshake line that looks opponents in the eye, a two‑minute litter sweep after away games. Invite feedback monthly via anonymous cards: “what should we stop, start, continue?” Real culture is repeated until it becomes background noise — and that’s the point.
//
//Статья 9:
//
//From 4‑4‑2 to 3‑2‑5 in Two Weeks
//Tactical shifts stick when roles, not shapes, are crystal clear. Explain that fullbacks tuck in to form a back three in build‑up while wingers pin the last line. Train exit routes: CB → inverted FB → 8 between lines; if the 8 is blocked, bounce to the weak‑side winger; if pressed, recycle and switch. Use constraints — goals count double after a switch of play or a third‑man run. Define triggers: back pass = push fullbacks inside; winger receives to feet = opposite 8 runs beyond; trapped wide = emergency underlap. Rehearse against a press twice per week with a shot clock and score for breaking lines. The chalkboard isn’t the point; repeatable cues under fatigue are
//
//Статья 10:
//
//Goalkeeper as Playmaker
//If your keeper only saves, you play 10v11 in build‑up. Teach scan‑set‑sell: scan before the ball arrives, set for two options, sell a fake before passing. Measure success by how often the first pass breaks a line and how many times the press is baited then bypassed. Drill clipped passes to the weak‑side fullback and disguised punches into the 6. Train under fatigue at the end of sessions and demand immediate recovery runs after giveaways. A brave, well‑coached keeper adds a midfielder without a substitution and makes pressing you expensive
//
//Статья 11:
//
//Youth Integration That Sticks
//Promote one academy player each month instead of four at once. Assign a senior mentor and a micro‑role (late pressing sub, near‑post marker, high‑energy 15 minutes). Avoid debuts at 0–3 down. Share a pathway document: minutes target, training objectives, and review date. Protect exam periods and travel stress. Celebrate firsts with a simple photo, not a speech, and debrief quietly after. Quiet normality is growth fuel; hype burns quickly, process builds careers
//
//
//Статья 12:
//
//Post‑Match Reviews in 12 Minutes
//Right after full time, capture three clips: a chance created, a chance conceded, and a set piece. On Monday, watch them together and ask three questions: what created this, what repeats, and what one change fixes it? Assign one training constraint that directly targets the issue and schedule it for Tuesday. End with a single battleground for the week. Reviews should feel like springboards, not trials, and they should fit busy lives — when they do, players return, learn, and improve.
